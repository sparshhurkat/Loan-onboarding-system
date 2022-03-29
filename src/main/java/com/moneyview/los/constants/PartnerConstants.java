package com.moneyview.los.constants;

import java.util.ArrayList;
import java.util.Arrays;

import com.moneyview.los.model.PartnerEntity;

public class PartnerConstants {

	private static ArrayList<PartnerEntity> partnerModelList = new ArrayList<PartnerEntity>(
			Arrays.asList(
				    new PartnerEntity(
			                1,3000.0,20000.0, 27.0, "WhizdmLOC"),
				    new PartnerEntity(2, 3000.0, 100000.0, 25.0, "DMI"),new PartnerEntity(3, 3000.0, 150000.0, 20.0, "Fullerton"),new PartnerEntity(4, 3000.0, 300000.0, 20.0, "DMI"),new PartnerEntity(5, 3000.0, 400000.0, 17.0, "IIFL"),new PartnerEntity(6, 3000.0, 500000.0, 17.0, "WhizdmLOC")));
	
	


	public static PartnerEntity getPartnerModel(int index) {
		return getPartnerModelList().get(index);
	}




	public static ArrayList<PartnerEntity> getPartnerModelList() {
		return partnerModelList;
	}




	public static void setPartnerModelList(ArrayList<PartnerEntity> partnerModelList) {
		PartnerConstants.partnerModelList = partnerModelList;
	}

	
	
}
