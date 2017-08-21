package com.vivi.entities;

import java.util.HashSet;
import java.util.Set;

public class Station {
	
	private String name;
	
	private Set<Zone> zones = new HashSet<>();
	
	public Station(String name, Set<Zone> zoneList) {
		this.name = name;
		this.zones = zoneList;
	}
	 
	public Set<Zone> getZones() {
		return zones;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}