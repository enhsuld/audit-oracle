package com.nbb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nbb.models.fn.LutForm;
import com.nbb.repository.LutFormRepository;

@Service
public class FormServiceImpl implements FormService {

	@Autowired
    private LutFormRepository dao;
	
	 @Override
     public Page<LutForm> findPaginated(int page, int size) {
        return dao.findAll(new PageRequest(page, size));
     }

}
