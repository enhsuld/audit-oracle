/**
 * 
 */
package com.nbb.util.pagination;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * The Class DataTableResults.
 *
 * @author pavan.solapure
 * @param <T> the generic type
 */
public class DataTableResults {
	
	/** The draw. */
	private int draw;
	
	/** The records filtered. */
	private int recordsFiltered;
	
	/** The records total. */
	private int recordsTotal;
	
	private int totalRecords;
	

	/** The list of data objects. */
	@SerializedName("data")
	List<?> data;
	
	
	/*public int getJson() {
		return new Gson().toJson(this);
	}*/

	/**
	 * Gets the draw.
	 *
	 * @return the draw
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * Sets the draw.
	 *
	 * @param draw the draw to set
	 */
	public void setDraw(int draw) {
		this.draw = draw;
	}

	/**
	 * Gets the records filtered.
	 *
	 * @return the recordsFiltered
	 */
	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	/**
	 * Sets the records filtered.
	 *
	 * @param recordsFiltered the recordsFiltered to set
	 */
	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	/**
	 * Gets the records total.
	 *
	 * @return the recordsTotal
	 */
	public int getRecordsTotal() {
		return recordsTotal;
	}

	/**
	 * Sets the records total.
	 *
	 * @param recordsTotal the recordsTotal to set
	 */
	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * Gets the list of data objects.
	 *
	 * @return the listOfDataObjects
	 */
	/*public List<?> getListOfDataObjects() {
		return listOfDataObjects;
	}*/

	/**
	 * Sets the list of data objects.
	 *
	 * @param listOfDataObjects the listOfDataObjects to set
	 */
/*	public void setListOfDataObjects(List<?> listOfDataObjects) {
		this.listOfDataObjects = listOfDataObjects;
	}*/
	
	
	
}

