package com.canigenus.common.model;

import java.util.List;

public interface MongoTransactionable {

	List<String> getPendingTransactions();
	void setPendingTransactions(List<String> pendingTransactions);
	
}
