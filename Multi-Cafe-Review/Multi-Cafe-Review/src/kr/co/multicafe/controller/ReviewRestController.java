package kr.co.multicafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.multicafe.dto.Review;
import kr.co.multicafe.dto.ReviewLike;
import kr.co.multicafe.service.ReviewService;

@RestController
@RequestMapping(path="/api/review")
public class ReviewRestController {

	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/{menuId}")
	public List<Review> listViewReview(@PathVariable(name="menuId")int menuId){
		return reviewService.listViewReview(menuId);
	}
	
//	@GetMapping("/{reviewId}")
//	public Review getReview(int reviewId) {
//		return reviewService.getReview(reviewId);
//	}
	
	@GetMapping("/good/{menuId}")
	public List<Review> goodListReview(@PathVariable(name="menuId")int menuId){
		return reviewService.goodListReview(menuId);
	}

}
