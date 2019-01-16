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
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException, InterruptedException  {
		// Recherche du plus vieux de nos ct
		List<LXC> cts = api.getCTs(srcServer);
		long timerunning = 0;
		long id = -1;
		for( LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				if (lxc.getUptime()> timerunning){
					timerunning=lxc.getUptime();
					id = Integer.parseInt(lxc.getVmid());
				}
			}
		}
		// migration du ct
		System.out.println(Long.toString(id));
		if (id>0) {
			System.out.println("Arret du CT : "+ Long.toString(id));
			api.stopCT(srcServer, Long.toString(id));
			Thread.sleep(5000);
			System.out.println("Migration du CT : "+ Long.toString(id));
			String idCt = Long.toString(id);
			api.migrateCT(srcServer, idCt, dstServer);
			System.out.println("migration in progress ...");
			Thread.sleep(20000);
			api.startCT(dstServer, idCt);
			Thread.sleep(5000);
		}
		

	}

	// arr�ter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException, InterruptedException {
		// Recherche du plus vieux de nos ct
		List<LXC> cts = api.getCTs(server);
		long timerunning = 0;
		long id = -1;
		for( LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				if (lxc.getUptime()> timerunning){
					timerunning=lxc.getUptime();
					id = Integer.parseInt(lxc.getVmid());
				}
			}
		}
		// Arret du CT
		if (id>0) {
			System.out.println("Arret du CT : "+ Long.toString(id));
			api.stopCT(server, Long.toString(id));
			Thread.sleep(5000);
		}
	}

}
