package com.project.bidding.api.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.bidding.api.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long>{


	Object findAllByCategoryIn(ArrayList<String> selectedCategory);

	ArrayList<Auction> findAllByCategory(String selectedCategory);

	Auction findByeventNo(long eventNo);

	

}
