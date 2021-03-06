package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

import com.sun.corba.se.impl.orbutil.closure.Constant;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	public void getInfosmyCts(Map<String, List<LXC>> myCTsPerServer) throws InterruptedException, LoginException, JSONException, IOException  {
		
		// SERVER 1
		System.out.println("SERVER "+Constants.SERVER1);
		List<LXC> CTs1 = myCTsPerServer.get(Constants.SERVER1);
		for ( LXC lxc :CTs1) {
			System.out.println("   CT : "+lxc.getName());
			System.out.println("        id : "+lxc.getVmid());
		
			float cpuMax1 = (float)(lxc.getMaxmem() );
			float cpuUsage =(float)(lxc.getMem());
			System.out.println("        Mem usage : "+ Float.toString(cpuUsage/cpuMax1*100) + " %");
			
			float diskMax = (float)(lxc.getMaxdisk());
			float diskUse = (float)(lxc.getDisk());
			System.out.println("        DISK usage : "+ Float.toString(diskUse/diskMax*100) + " %");

			
			float cpuUse = (float)(lxc.getCpu());
			System.out.println("        CPU usage : "+ Float.toString((cpuUse)*100) + " %");
				
		}	
		
		// SERVER 2
		System.out.println("SERVER "+Constants.SERVER2);
		List<LXC> CTs2 = myCTsPerServer.get(Constants.SERVER2);
		for ( LXC lxc :CTs2) {
			System.out.println("   CT : "+lxc.getName());
			System.out.println("        id : "+lxc.getVmid());
		
			float cpuMax2 = (float)(lxc.getMaxmem() );
			float cpuUsage =(float)(lxc.getMem());
			System.out.println("        Mem usage : "+ Float.toString(cpuUsage/cpuMax2*100) + " %");
			
			float diskMax = (float)(lxc.getMaxdisk());
			float diskUse = (float)(lxc.getDisk());
			System.out.println("        DISK usage : "+ Float.toString(diskUse/diskMax*100) + " %");

			
			float cpuUse = (float)(lxc.getCpu());
			System.out.println("        CPU usage : "+ Float.toString((cpuUse)*100) + " %");
				
		}	
		
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws InterruptedException, LoginException, JSONException, IOException  {
		System.out.println("Anayse en cours ...");
		// RAM utilisée par mes CTs
		long RamS1=0;
		long RamS2=0;
		List<LXC> CTs1 = myCTsPerServer.get(Constants.SERVER1);
		for ( LXC lxc :CTs1) {
			RamS1 += lxc.getMem();
		}
		List<LXC> CTs2 = myCTsPerServer.get(Constants.SERVER2);
		for ( LXC lxc :CTs2) {
			RamS2 += lxc.getMem();
		}
		long mem_serv1_8 =(long)(api.getNode(Constants.SERVER1).getMemory_total()*0.08);
		long mem_serv1_12 =(long)(api.getNode(Constants.SERVER1).getMemory_total()*0.12);
		long mem_serv1_3 =(long)(api.getNode(Constants.SERVER1).getMemory_total()*0.03);
		long mem_serv1_5 =(long)(api.getNode(Constants.SERVER1).getMemory_total()*0.05);
		
		long mem_serv2_8 =(long)(api.getNode(Constants.SERVER2).getMemory_total()*0.08);
		long mem_serv2_12 =(long)(api.getNode(Constants.SERVER2).getMemory_total()*0.12);
		long mem_serv2_3 =(long)(api.getNode(Constants.SERVER2).getMemory_total()*0.03);
		long mem_serv2_5 =(long)(api.getNode(Constants.SERVER2).getMemory_total()*0.05);
		
		float use_ram_mycts_server1 =((float)RamS1/(float)api.getNode(Constants.SERVER1).getMemory_total()*100);
		float use_ram_mycts_server2 =((float)RamS2/(float)api.getNode(Constants.SERVER2).getMemory_total()*100);
		System.out.println(" Utilisation de nos CTs sur le server 5 : "+ Float.toString(use_ram_mycts_server1)+ " %");
		System.out.println(" Utilisation de nos CTs sur le server 6 : "+ Float.toString(use_ram_mycts_server2)+ " %");
		
		
		// action en fonction de la memoire utilisee
		
		Controller controller = new Controller(api);
		if (RamS1 > mem_serv1_5) {
			controller.offLoad(Constants.SERVER1);
			System.out.println(" Arret CT sur server 5");
		} else if (RamS2 >mem_serv2_5) {
			controller.offLoad(Constants.SERVER2);
			System.out.println(" Arret CT sur server 6");
		} else if ( (RamS1>mem_serv1_3)&&(RamS2>mem_serv2_3)) {
			if (RamS1 > RamS2) {
				controller.migrateFromTo(Constants.SERVER1, Constants.SERVER2);
			}else {
				controller.migrateFromTo(Constants.SERVER2, Constants.SERVER1);
			}
		} else if ( RamS1 > mem_serv1_3) {
			controller.migrateFromTo(Constants.SERVER1, Constants.SERVER2);
			System.out.println("Migration de 5 vers 6");
		} else if ( RamS2 > mem_serv1_3) {
			controller.migrateFromTo(Constants.SERVER2, Constants.SERVER1);
			System.out.println("Migration de 6 vers 5");
		}else {
			System.out.println(" No action to do ");
		}
	
	
	
	}

}
