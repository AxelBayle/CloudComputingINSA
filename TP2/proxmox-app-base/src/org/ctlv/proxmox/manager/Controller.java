package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException  {
		// Recherche du plus vieux de nos ct
		List<LXC> cts = api.getCTs(srcServer);
		long timerunning = 0;
		long id = -1;
		for( LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				if (lxc.getUptime()> timerunning){
					timerunning=lxc.getUptime();
					id = lxc.getPid();
				}
			}
		}
		// migration du ct
		if (id>0) {
			String idCt = Long.toString(id);
			api.migrateCT(srcServer, idCt, dstServer);
		}
		

	}

	// arr�ter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException {
		// Recherche du plus vieux de nos ct
		List<LXC> cts = api.getCTs(server);
		long timerunning = 0;
		long id = -1;
		for( LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				if (lxc.getUptime()> timerunning){
					timerunning=lxc.getUptime();
					id = lxc.getPid();
				}
			}
		}
		// Arret du CT
		if (id>0) {
			api.stopCT(server, Long.toString(id));
		}
	}

}
