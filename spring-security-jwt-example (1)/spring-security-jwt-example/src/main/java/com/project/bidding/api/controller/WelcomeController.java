package com.project.bidding.api.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.project.bidding.api.entity.Auction;
import com.project.bidding.api.entity.AuthRequest;
import com.project.bidding.api.entity.Bidder;
import com.project.bidding.api.entity.Catalog;
import com.project.bidding.api.entity.Seller;
import com.project.bidding.api.repository.AuctionRepository;
import com.project.bidding.api.repository.CatalogRepository;
import com.project.bidding.api.repository.CategoryRepository;
import com.project.bidding.api.repository.SellerRepository;
import com.project.bidding.api.service.BidderService;
import com.project.bidding.api.service.SellerService;
import com.project.bidding.api.util.JwtUtil;


@Controller
@CrossOrigin(origins="*")
public class WelcomeController {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CatalogRepository catalogRepository;
	@Autowired
	AuctionRepository auctionRepository;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private BidderService bidderService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AuthenticationManager authenticationManager;

	public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/webapp/auctionimage";

	public static String uploadDirectoryForCatalog = System.getProperty("user.dir") + "/src/main/webapp/catalogimage";

	
	
	@RequestMapping(value="/auctionhouse/login" , method=RequestMethod.GET)
	public String login() {
		return "auctioneer-login";
	}

	
	@RequestMapping(value="/auctionhouse/signup" , method=RequestMethod.GET)
	public String signup() {
		return "auctioneer-signup";
	}
                                                                        
	                                                                                                             
    @RequestMapping(value="/auctionhouse/signup" , method=RequestMethod.POST)
    @ResponseBody
    public String signUpAsAuctioneer(@ModelAttribute Seller seller) {
    	if(sellerService.checkIfSellerEmailIdAlreadyExistInTheDatabase(seller)) {//email exist in the database
    		return "failure";
    	}
    	else {
    		sellerService.saveSeller(seller);
    		return "success";
    	}
    }
    

    
    
	
	
	@RequestMapping(value="/auctionhouse/addauction" , method=RequestMethod.GET)
	public String getauction() {
		return "Auction";
	}

	
	
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(2000000);
		return multipartResolver;
	}
	
	
	
	@RequestMapping(value="/auctionhouse/addauction" ,method = RequestMethod.POST)
	@ResponseBody
	public String saveStudent(@ModelAttribute Auction auction, @RequestParam("imgName") MultipartFile file ) {


		String filename=file.getOriginalFilename().substring(file.getOriginalFilename().length()-4);
		Path fileNameAndPath =Paths.get(uploadDirectory,filename);

		try {
			Files.write(fileNameAndPath, file.getBytes());
		} catch (IOException e) {

			e.printStackTrace();
		}


		auction.setImageName(filename);
		auctionRepository.save(auction);

		return "Save Data Successfully ! ";
	}
	
	
	

	@RequestMapping(value="/auctionhouse/catalog" ,method = RequestMethod.POST)
	@ResponseBody
	public String saveCatalogInfo(@RequestParam("itemName") ArrayList<String> itemName, @RequestParam("itemImage") ArrayList<MultipartFile> file, @RequestParam("itemStartBid") ArrayList<Integer> itemStartBid, @RequestParam("itemDesc") ArrayList<String> itemDesc  ) {
		int n=itemName.size();
		for (int i = 0; i < n; i++) {
			Catalog c=new Catalog();
			c.setItemDesc(itemDesc.get(i));
			c.setItemName(itemName.get(i));
			c.setItemStartBid(itemStartBid.get(i));
			MultipartFile f=file.get(i);
			String filename=f.getOriginalFilename();
			Path fileNameAndPath =Paths.get(uploadDirectoryForCatalog,filename);
			try {
				Files.write(fileNameAndPath, f.getBytes());
			} catch (IOException e) {

				e.printStackTrace();
			}

			c.setItemImage(filename);

			catalogRepository.save(c);
		} 
		return "Saved Data in catalog Successfully ! ";
	}



	@RequestMapping(value="/auctionhouse/auction" , method=RequestMethod.GET)
	public String getfullauction(Model model) {
		model.addAttribute("categories", categoryRepository.findAll());
		return "Auction-catalog";
	}



	@RequestMapping(value="/auctionhouse/auction" ,method = RequestMethod.POST)
	@ResponseBody
	public String saveauction(HttpServletRequest httpServletRequest,@ModelAttribute Auction auction, @RequestParam("imgName") MultipartFile file1 ,@RequestParam("itemName") ArrayList<String> itemName, @RequestParam("itemImage") ArrayList<MultipartFile> file, @RequestParam("itemStartBid") ArrayList<Integer> itemStartBid, @RequestParam("itemDesc") ArrayList<String> itemDesc) {

		try {
			String filename1=file1.getOriginalFilename();
			Path fileNameAndPath1 =Paths.get(uploadDirectory,filename1);

			try {
				Files.write(fileNameAndPath1, file1.getBytes());
			} catch (IOException e) {

				e.printStackTrace();
				}

			auction.setImageName(filename1);
			String authorizationHeader = null;
	    	 Cookie[] cookies=httpServletRequest.getCookies();
			
	    	 for(Cookie c :cookies )
	    	 {
	    		 if(c.getName().equals("token"))
	    		 {
	    			 authorizationHeader=c.getValue();
	    		 }
	    	 }
			auction.setSellerId(jwtUtil.extractUsername(authorizationHeader));

			//   		-----------------------------------------------------------------------------------------------------------------

			ArrayList<Catalog> catlist=new ArrayList<>();
			int n=itemName.size();
			for (int i = 0; i < n; i++) {
				Catalog c=new Catalog();
				c.setItemDesc(itemDesc.get(i));
				c.setItemName(itemName.get(i));
				c.setItemStartBid(itemStartBid.get(i));
				MultipartFile f=file.get(i);
				String filename=f.getOriginalFilename();
				Path fileNameAndPath =Paths.get(uploadDirectoryForCatalog,filename);
				try {
					Files.write(fileNameAndPath, f.getBytes());
				} catch (IOException e) {

					e.printStackTrace();
				}

				c.setItemImage(filename);

				catlist.add(c);
				//catalogRepository.save(c);
			} 

			auction.setItems(catlist);



			auctionRepository.save(auction);

			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "failure";
			
		}
	}
	
	
	

	@RequestMapping(value=  "/authenticate", method=RequestMethod.POST)
	public String generateToken(Model model, @ModelAttribute AuthRequest authRequest, HttpServletRequest request,HttpServletResponse response) throws Exception {

		System.out.println(authRequest.getUserName());
		System.out.println(authRequest.getPassword());
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
					);
		} catch (Exception ex) {
			throw new Exception("inavalid username/password");


		}

		System.out.println(jwtUtil.generateToken(authRequest.getUserName()));

		Cookie cookie = new Cookie("token",jwtUtil.generateToken(authRequest.getUserName()));
		cookie.setMaxAge(60 * 60 * 10);
		response.addCookie(cookie);
		// HttpSession session = request.getSession();
		// session.setAttribute("token", jwtUtil.generateToken(authRequest.getUserName()));
		//response.sendRedirect("/welcome");
		model.addAttribute("auctions", auctionRepository.findAll());
		return "auctioneer-dashboard";

	}

	@RequestMapping(value="/authenticateBidder", method=RequestMethod.POST)
	public void generateTokenForBidder(@ModelAttribute AuthRequest authRequest, HttpServletRequest request,HttpServletResponse response) throws Exception {

		System.out.println(authRequest.getUserName());
		System.out.println(authRequest.getPassword());
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
			);
		} catch (Exception ex) {
			throw new Exception("inavalid username/password");


		}

//		System.out.println(jwtUtil.generateTokenForBidder(authRequest.getUserName()));

		Cookie cookie = new Cookie("token",jwtUtil.generateTokenForBidder(authRequest.getUserName()));
		cookie.setMaxAge(60 * 60 * 10);
		response.addCookie(cookie);
		// HttpSession session = request.getSession();
		// session.setAttribute("token", jwtUtil.generateToken(authRequest.getUserName()));
		response.sendRedirect("/bidder/dashboard/");

		//return "dashboard";

	}
	
	
	
	@RequestMapping(value="/auctionhouse/catalog" , method=RequestMethod.GET)
	public String navigateToCatalogAfterAuctioneerSignup() {  
		return "auctionhouse-catalog"; 
	}

	  
    @RequestMapping(value="/auctionhouse/dashboard" , method=RequestMethod.GET)
	public String auctioneerDashboardGet(Model model) {
    	
    	
    	model.addAttribute("auctions", auctionRepository.findAll());
//    	model.addAttribute("categories", categoryRepository.findAll());
    	
		return "auctioneer-dashboard";
		/*
		 
		      @RequestMapping(value="/bidder/event/{eventno}" , method=RequestMethod.GET)
    public String bidderEventPageGet(@PathVariable("eventno") long eventNo, Model model) {
    	
    	model.addAttribute("items", auctionRepository.findByeventNo(eventNo));
    	Auction a = (Auction) auctionRepository.findByeventNo(eventNo);
    	model.addAttribute("catalog", a.getItems());
    	return "event";
    }
    
    @RequestMapping(value="/bidder/event/" , method=RequestMethod.POST)
    public String bidderEventPagePost() {
    return "event";
    }
		  
		  */
	}


    
    @RequestMapping(value="/auctionhouse/event/{eventno}" , method=RequestMethod.GET)
    public String auctioneerEventPageGet(@PathVariable("eventno") long eventNo, Model model) {
    	
    	model.addAttribute("items", auctionRepository.findByeventNo(eventNo));//items will have the list of items so will hagve to implement foreeach lopp in jsp page
    	Auction a = (Auction) auctionRepository.findByeventNo(eventNo);
    	model.addAttribute("catalog", a.getItems());
    	model.addAttribute("eventNo",eventNo);
    	return "auctioneer-event";
    }

	/* ------------------------------ Below code is for bidder  ------------------------------ */


	@RequestMapping(value="/bidder/signup" , method=RequestMethod.GET)
	public String bidderSignUp() {
		return "bidder-signup";
	}

	@RequestMapping(value="/bidder/signup" , method=RequestMethod.POST)
	public String bidderSignInAfterSignUp(@ModelAttribute Bidder bidder) {

		bidderService.bidderSignUp(bidder);
		return "bidder-login";
	}

	@RequestMapping(value="/bidder/login" , method=RequestMethod.GET)
	public String bidderLogin() {
		return "bidder-login";
	}

    @RequestMapping(value="/bidder/dashboard" , method=RequestMethod.GET)
    public String bidderdashboard(Model model) {
    	model.addAttribute("auctions", auctionRepository.findAll());
    	model.addAttribute("categories", categoryRepository.findAll());
        return "dashboard";
    }
    
    @RequestMapping(value="/bidder/dashboard/" , method=RequestMethod.POST)
    public String postitembycategories(@RequestParam("checkbox") ArrayList<String> selectedCategory, Model model) {
    	
    	for(String a: selectedCategory)
    	{
    		System.out.println(a);
    	}
    	ArrayList<Auction> result =new ArrayList<>();
    
    	for(int i=0;i<selectedCategory.size();i++)
    	{
    		result.addAll( auctionRepository.findAllByCategory(selectedCategory.get(i)));
    	}
    	model.addAttribute("auctions", result);
    	System.out.println(result);
    	
    //	System.out.println(auctionRepository.findAllByCategoryIn(s));
    	model.addAttribute("categories", categoryRepository.findAll());
    	
        return "dashboard";
    }


	@RequestMapping(value = "/bidder/event/{eventno}", method = RequestMethod.GET)
	public String bidderEventPageGet(@PathVariable("eventno") long eventNo, Model model) {

		Auction current_auction = auctionRepository.findByeventNo(eventNo);
		model.addAttribute("items", current_auction);

		model.addAttribute("eventNumber", eventNo);
		System.out.println(sellerRepository.findByEmail(current_auction.getSellerId()));
		model.addAttribute("auctionHouseName", sellerRepository.findByEmail(current_auction.getSellerId()).getHouseName());
		Auction a = (Auction) auctionRepository.findByeventNo(eventNo);


//    	Auction a = (Auction) auctionRepository.findByeventNo(eventNo);
		model.addAttribute("catalog", a.getItems());
		return "event";
	}
    
    @RequestMapping(value="/bidder/event/" , method=RequestMethod.POST)
    public String bidderEventPagePost() {
    return "event";
    }
    
    
   
   
    
}
