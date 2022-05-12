use std::io::Result;
use std::process::exit;
use std::{env, io};

use deelevate::{BridgeServer, PrivilegeLevel, Token};

use crate::privileges::can_create_symlinks;

pub fn with_symlink_permission<T>(run: T)
where
    T: Fn() -> Result<()>,
{
    let result = if can_create_symlinks() {
        run()
    } else {
        let result = with_privileges(run);
        if let Err(e) = &result {
            if let Some(code) = e.raw_os_error() {
                exit(code);
            }
        }
        result
    };

    result.unwrap();
}

pub fn with_privileges<T>(run: T) -> Result<()>
where
    T: Fn() -> Result<()>,
{
    let token = Token::with_current_process()?;
    match token.privilege_level()? {
        PrivilegeLevel::NotPrivileged => match spawn_with_elevated_privileges() {
            Err(e) => match e.raw_os_error() {
                None => panic!("{}", e),
                Some(code) => exit(code),
            },
            Ok(_code) => Ok(()),
        },
        PrivilegeLevel::Elevated | PrivilegeLevel::HighIntegrityAdmin => run(),
    }
}

fn spawn_with_elevated_privileges() -> Result<()> {
    let token = Token::with_current_process()?;
    let level = token.privilege_level()?;

    let target_token = match level {
        PrivilegeLevel::NotPrivileged => token.as_medium_integrity_safer_token()?,
        PrivilegeLevel::HighIntegrityAdmin | PrivilegeLevel::Elevated => return Ok(()),
    };

    let mut server = BridgeServer::new();
    let mut argv = env::args_os().collect();
    let mut bridge_cmd = server.start_for_command(&mut argv, &target_token)?;
    let proc = bridge_cmd.shell_execute("runas");
    match proc {
        Ok(process) => server.serve(process).map(|_| ()),
        Err(_) => Err(io::Error::last_os_error()),
    }
}
