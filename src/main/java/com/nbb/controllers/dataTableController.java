package com.nbb.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nbb.dao.UserDao;
import com.nbb.models.fn.LutForm;
import com.nbb.services.FormService;
import com.nbb.util.AppUtil;
import com.nbb.util.MyResourceNotFoundException;
import com.nbb.util.PageConstant;
import com.nbb.util.PageUtil;
import com.nbb.util.PaginatedResult;
import com.nbb.util.pagination.DataTableRequest;
import com.nbb.util.pagination.DataTableResults;
import com.nbb.util.pagination.PaginationCriteria;

@RestController
public class dataTableController {
	
	@Autowired
    private UserDao dao;
	
	@RequestMapping(value="/form/paginated", method=RequestMethod.GET)
	public @ResponseBody DataTableResults listUsersPaginated(HttpServletRequest request, HttpServletResponse response) {
		
		DataTableRequest<LutForm> dataTableInRQ = new DataTableRequest<LutForm>(request);
		PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();
		String str= (AppUtil.isObjectEmpty(pagination.getFilterByClause())) ? "" : " WHERE " + pagination.getFilterByClause();
		
		@SuppressWarnings("unchecked")
		List<LutForm> userList =(List<LutForm>) dao.jData(pagination.getPageSize(), pagination.getPageNumber(), pagination.getOrderByClause(), str,  "LutForm");
		int count=dao.jDataCount(str,  "LutForm");
		int total=dao.jDataCount("",  "LutForm");

		DataTableResults dataTableResult = new DataTableResults();
		dataTableResult.setDraw(Integer.parseInt(dataTableInRQ.getDraw()));
		dataTableResult.setData(userList);
		dataTableResult.setTotalRecords(count);
		if (!AppUtil.isObjectEmpty(userList)) {
			dataTableResult.setRecordsTotal(total);
			if (dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
				dataTableResult.setRecordsFiltered(count);
			} else {
				dataTableResult.setRecordsFiltered(count);
			}
		}
		return dataTableResult;
	}
	
    @Autowired
    private FormService service;
	
    @RequestMapping(value = "api/form/list", method = RequestMethod.GET, produces = "application/json")
    public Page<?> findPaginated(@RequestParam("pageNumber") int page, @RequestParam("pageSize") int size) {

        System.out.println("page"+page);
        System.out.println("page"+size);
        String query="";
        String order="";
        int pageSize = PageUtil.parsePage(String.valueOf(page), PageConstant.PAGE);
        int perPage = PageUtil.parsePerPage(String.valueOf(size), PageConstant.PER_PAGE);
        System.out.println(pageSize);
        System.out.println(perPage);
        
        Page<LutForm> resultPage = service.findPaginated(page, size);
        if (page > resultPage.getTotalPages()) {
            throw new MyResourceNotFoundException();
        }
        return resultPage;
    }
	
	
}
