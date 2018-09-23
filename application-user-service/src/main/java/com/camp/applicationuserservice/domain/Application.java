package com.camp.applicationuserservice.domain;

public class Application {

	private String id;
	private String name;
	private String packageName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", name=" + name + ", packageName=" + packageName + "]";
	}

}
