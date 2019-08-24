package com.aloha.tds;

import java.util.List;

public interface TourLibrary {
	List<Tour> toursByCustomer(Customer customer);
}
