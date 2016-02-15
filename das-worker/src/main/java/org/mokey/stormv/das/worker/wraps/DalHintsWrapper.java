package org.mokey.stormv.das.worker.wraps;

import java.util.HashMap;
import java.util.Map;

import org.mokey.stormv.das.models.DalModels.DalHint;
import org.mokey.stormv.das.models.DalModels.DalHintEnum;
import org.mokey.stormv.das.models.DalModels.DalHints;

public class DalHintsWrapper {
	private Map<DalHintEnum, String> hints;
	private DalHints dalHints;
	
	public DalHintsWrapper(DalHints hints){
		this.dalHints = hints;
		this.hints = new HashMap<DalHintEnum, String>();
		if(hints != null && hints.getHintsList().size() > 0){
			for (DalHint hint : this.dalHints.getHintsList()) {
				this.hints.put(hint.getKey(), hint.getValue());
			}
		}
	}
	
	public DalHintsWrapper clone(){
		DalHints newHints = this.dalHints.toBuilder().build();
		return new DalHintsWrapper(newHints);
	}
	
	public DalHints getDalHints(){
		return this.dalHints;
	}
	
	public void cleanUp(){
		this.hints.clear();
	}
	
	public boolean is(DalHintEnum hint) {
		return hints.containsKey(hint);
	}
	
	public Integer getInt(DalHintEnum hint, int defaultValue) {
		String value = hints.get(hint);
		if(value == null)
			return defaultValue;
		return Integer.parseInt(value);
	}
	
	public Integer getInt(DalHintEnum hint) {
		if(!hints.containsKey(hint))
				return null;
		return Integer.parseInt(hints.get(hint));
	}
	
	public DalHintsWrapper set(DalHintEnum hint, Integer val){
		this.hints.put(hint, val.toString());
		return this;
	}
	
	public String getString(DalHintEnum hint) {
		return this.hints.get(hint);
	}
	
	public DalHintsWrapper set(DalHintEnum hint, String value) {
		hints.put(hint, value);
		return this;
	}
}
