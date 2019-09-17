package com.nbb.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nbb.models.FileUpload;
import com.nbb.models.fn.LutForm;
import com.nbb.repository.FileUploadRepository;
import com.nbb.repository.LutFormRepository;



public interface FormService extends IOperations<LutForm> {

}