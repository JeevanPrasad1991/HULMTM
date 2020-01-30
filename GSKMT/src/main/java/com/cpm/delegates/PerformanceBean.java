package com.cpm.delegates;

public class PerformanceBean {
	public String store_id;
	public String period;

	public String getSOS_SCORE() {
		return SOS_SCORE;
	}

	public void setSOS_SCORE(String SOS_SCORE) {
		this.SOS_SCORE = SOS_SCORE;
	}

	public String getSTOCK_SCORE() {
		return STOCK_SCORE;
	}

	public void setSTOCK_SCORE(String STOCK_SCORE) {
		this.STOCK_SCORE = STOCK_SCORE;
	}

	public String getASSET_SCORE() {
		return ASSET_SCORE;
	}

	public void setASSET_SCORE(String ASSET_SCORE) {
		this.ASSET_SCORE = ASSET_SCORE;
	}

	public String getPROMO_SCORE() {
		return PROMO_SCORE;
	}

	public void setPROMO_SCORE(String PROMO_SCORE) {
		this.PROMO_SCORE = PROMO_SCORE;
	}

	public String SOS_SCORE;
	public String STOCK_SCORE;
	public String ASSET_SCORE;
	public String PROMO_SCORE;
	public String pss_avg;
	public String process_id;
	

	
	public String getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String category_id;
	
	
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPss_avg() {
		return pss_avg;
	}
	public void setPss_avg(String pss_avg) {
		this.pss_avg = pss_avg;
	}
	

	

}
