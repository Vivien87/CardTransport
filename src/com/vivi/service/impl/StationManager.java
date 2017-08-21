package com.vivi.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vivi.entities.Station;
import com.vivi.entities.Zone;

import java.util.Set;
import java.util.TreeMap;

public class StationManager {
	
	private Map<String, Station> stations = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	public void initialize(Map<String, List<Integer>> stationNameWithZoneIds){
		if (stationNameWithZoneIds == null || stationNameWithZoneIds.size() == 0){
			throw new IllegalAccessError("System can't be initialized, there should be some stations to start with");
		}
		for(Entry<String, List<Integer>> station: stationNameWithZoneIds.entrySet()){
			Set<Zone> zoneList = new HashSet<>();
			List<Integer> zoneIds = station.getValue();
			if(zoneIds==null || zoneIds.isEmpty()){
				throw new IllegalArgumentException("System can't be initialized, each station should come under at least one zone");
			}
			for(Integer zoneId: zoneIds){
				Zone zone = new Zone(zoneId);
				zoneList.add(zone);
			}
			String name = station.getKey();
			stations.put(name, new Station(name, zoneList));
		}
	}

	public Station get(String name) {
		return stations.get(name);
	}
	
}