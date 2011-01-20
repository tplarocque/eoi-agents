/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ooici.eoi.datasetagent.obs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.ooici.eoi.datasetagent.VariableParams;


/**
 * Standard implementation of the IObservationGroup interface, used for storage of observation groupings from one fixed geographic location
 * 
 * @author cmueller
 * @author tlarocque <tplarocque@asascience.com>
 */
public class ObservationGroupImpl extends AbstractObservationGroup {

	private List<Number> times = new ArrayList<Number>();
	private List<Number> depths = new ArrayList<Number>();
	private HashMap<VariableParams, HashMap<TimeDepthPair, Number>> obsMap =
		new HashMap<VariableParams, HashMap<TimeDepthPair, Number>>();

	
	/**
     * Constructs a new ObservationGroupImpl with the given identifying characteristics
     * 
     * @param id The identifier of this AbstractObservationGroup instance
     * @param stnid The identifier of the unit from which this AbstractObservationGroup's observations were made
     * @param lat The geographic latitude coordinate at which this AbstractObservationGroup's observations were made
     * @param lon The geographic longitude coordinate at which this AbstractObservationGroup's observations were made
     */
	public ObservationGroupImpl(int id, String stnid, Number lat, Number lon) {
		super(id, stnid, lat, lon);
	}

	/* NOTE: dataName should be a custom type so that it can hold more pertinent data,
	 * such as no-data-value.  As of current, an observation group may have only 1 missing data value, but different
	 * data items (variables) may have differing no-data-values
	 */
	@Override
	protected void _addObservation(Number time, Number depth, Number data, VariableParams dataAttribs) {
		if (!depths.contains(depth)) {
			depths.add(depth);
		}
		if (!times.contains(time)) {
			times.add(time);
		}
		/* FIXME: be careful here, if two dataName variables are given which represent the same
		 * variable but are created anew before being passed to addObservation(), they will be viewed as two
		 * separate dataNames.  This is because when two pairs are compared via equals() they can only compare
		 * based on the hashcode of their data values.  If the memory address of the values differ, the pair will not
		 * be considered equal.
		 */
		if (!obsMap.containsKey(dataAttribs)) {
			obsMap.put(dataAttribs, new HashMap<TimeDepthPair, Number>());
		}
		TimeDepthPair cp = new TimeDepthPair(time, depth);
		obsMap.get(dataAttribs).put(cp, data);
	}

	@Override
	public int getNumObs() {
		return obsMap.get(obsMap.keySet().iterator().next()).size();
	}

	public Number[] getTimes() {
		return times.toArray(new Number[times.size()]);
	}
	
	public <T> T[] getTimes(T[] array) {
	    return times.toArray(array);
	}

	public Number[] getDepths() {
	    return depths.toArray(new Number[depths.size()]);
	}
	
	public <T> T[] getDepths(T[] array) {
	    return depths.toArray(array);
	}

	public Number getData(VariableParams dataAttribs, Number time, Number depth) {
		return getData(dataAttribs, time, depth, Float.NaN);
	}

	public Number getData(VariableParams dataAttribs, Number time, Number depth, Number missingVal) {
		Number ret = missingVal;
		TimeDepthPair cp = new TimeDepthPair(time, depth);
		HashMap<TimeDepthPair, Number> map = obsMap.get(dataAttribs);
		if (map != null) {
			Number fval = map.get(cp);
			if (fval != null) {
				ret = fval.floatValue();
			}
		}
		return ret;
	}

	public List<VariableParams> getDataNames() {
		List<VariableParams> ret = new ArrayList<VariableParams>();
		for (VariableParams p : obsMap.keySet()) {
			ret.add(p);
		}
		return ret;
	}

}
