package cz.krtinec.birthday.dto;

public class BContactParent {

	protected String displayName;
	protected long id;
	protected String lookupKey;
	protected String photoId;
	
	
	public BContactParent(String displayName, long id, String lookupKey, String photoId) {
		this.displayName = displayName;
		this.id = id;
		this.lookupKey = lookupKey;
		this.photoId = photoId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public long getId() {
		return id;
	}
	public String getPhotoId() {
		return photoId;
	}
	public String getLookupKey() {
		return lookupKey;
	}
	

}
