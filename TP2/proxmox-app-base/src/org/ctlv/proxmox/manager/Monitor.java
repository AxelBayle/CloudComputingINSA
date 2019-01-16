package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		
		while(true) {
			
			Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();
			
			List<LXC> ctserver_1;
			try {
				ctserver_1 = api.getCTs(Constants.SERVER1);
			
			List<LXC> myctserver_1 = new ArrayList<LXC> () ;
			for ( LXC lxc : ctserver_1) {
				if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
					myctserver_1.add(lxc);
				}
			}
			
			List<LXC> ctserver_2 = api.getCTs(Constants.SERVER2);
			List<LXC> myctserver_2 = new ArrayList<LXC> () ;
			for ( LXC lxc : ctserver_2) {
				if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
					myctserver_2.add(lxc);
				}
			}
			
			myCTsPerServer.put(Constants.SERVER1, myctserver_1);
			myCTsPerServer.put(Constants.SERVER2, myctserver_2);
			

			
			analyzer.getInfosmyCts(myCTsPerServer);
			analyzer.analyze(myCTsPerServer);
			} catch (LoginException | JSONException | InterruptedException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
			// attendre une certaine p�riode
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void deletemyCT() throws LoginException, JSONException, IOException, InterruptedException {
		
		List<LXC> ctserver_1 = api.getCTs(Constants.SERVER1);
		for ( LXC lxc : ctserver_1) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {				
				System.out.println("Deleting CT n°"+lxc.getVmid()+ " sur serveur "+ Constants.SERVER1);
				if( lxc.getStatus().equals("running")) {

					api.stopCT(Constants.SERVER1, lxc.getVmid());
					Thread.sleep(5000);
				}
				api.deleteCT(Constants.SERVER1, lxc.getVmid());	
				Thread.sleep(5000);
				System.out.println("CT deleted");
				}
				
			}	
		
		List<LXC> ctserver_2 = api.getCTs(Constants.SERVER2);
		for ( LXC lxc : ctserver_2) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {				
				System.out.println("Deleting CT n°"+lxc.getVmid()+ " sur serveur "+ Constants.SERVER2);
				if( lxc.getStatus().equals("running")) {

					api.stopCT(Constants.SERVER2, lxc.getVmid());
					Thread.sleep(5000);
				}
				api.deleteCT(Constants.SERVER2, lxc.getVmid());	
				Thread.sleep(5000);
				System.out.println("CT deleted");
				}
				
			}		
		System.out.println("Cleaning finished ! Good job !");
		
	}
}
	
		

