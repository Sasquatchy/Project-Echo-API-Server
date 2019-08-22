package org.raoul.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.raoul.service.FrameServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.java.Log;

@RestController
@RequestMapping("/frame")
@Log
@CrossOrigin
public class FrameController {
	
	@Autowired
	FrameServiceImpl fService;
	

	@ResponseBody
	@GetMapping(value="/download/{ufid}", produces="application/zip")
	public ResponseEntity<Resource> downloadZip (@PathVariable("ufid") String ufid){
		
		//https://stackoverflow.com/questions/27952949/spring-rest-create-zip-file-and-send-it-to-the-client
		

		log.info("download");
		
		/*
		//////////////////////////////////////////////////
		//make zip entry
		// use dummy data for now
		log.info("make zip entry");
		//dummy list
		List<Path> pathList = new ArrayList<>();
		pathList.add(Paths.get("C:","upload","slide1.jpg"));
		pathList.add(Paths.get("C:","upload","slide2.jpg"));
		pathList.add(Paths.get("C:","upload","slide3.jpg"));
		pathList.add(Paths.get("C:","upload","slide4.jpg"));
		log.info(pathList.toString());
		
		// list of path to list of zipEntry
        //ZipEntry e = new ZipEntry("x");
        
		//find photo 
		

		
		//making zip file
		log.info("make zip");
		Path zipFolderPath = Paths.get("C:","upload","temp");
		Path zipPath = null;
//		Path zipFilePath = zipFolderPath.resolve("send.zip");
		try {
			zipPath = Files.createTempFile(zipFolderPath,"temp_", ".zip");
			log.info(zipPath.toString());

			
//			zipPath = Files.createFile(zipFilePath);
//			log.info(zipFilePath.toString());
		} catch (IOException e2) {
			log.info(e2.toString());
		}
		

		try (
			OutputStream fout = Files.newOutputStream(zipPath);
//			OutputStream fout = Files.newOutputStream(zipFilePath);
			ZipOutputStream	zos = new ZipOutputStream(fout);
		){
			pathList.stream().filter(path-> path.toFile().exists()).forEach((path)->{
				try (FileInputStream fis= new FileInputStream(path.toFile());) {
					log.info("fis.toString(): "+fis.toString());
					log.info("path.getFileName().toString(): "+path.getFileName().toString());
					zos.putNextEntry(new ZipEntry(path.getFileName().toString()));
					
					byte[] bytes = new byte[1024];
		            int length;
		            while((length = fis.read(bytes)) >= 0) {
		                zos.write(bytes, 0, length);
		            }
				} catch (IOException e) {
					log.info(e.toString());
				}
			});
			
			log.info("close Entry");
			try {
				zos.closeEntry();
			} catch (IOException e) {
				log.info(e.toString());
				}
			
		} catch (IOException e1) {
			log.info(e1.toString());
		}
		
		log.info("send zip");
		File zipFile = zipPath.toFile();
		log.info(zipPath.toString());
		
		/////////////////////////////////////////////////////////////////////////////
		*/
		
		// org.springframework.core.io.Resource 을 통해서 zip파일 보내기
		
		File zipFile = fService.makeZipByUfid(ufid);
		
		Resource resource = new FileSystemResource(zipFile);
		
		String resourceName ="";
		try {
			resourceName = new String( resource.getFilename().getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			log.info(e.toString());
		}
		
		log.info("Resource: "+resourceName);
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition","attachment; filename="+resourceName);
		
//		
//		try {
//			Files.delete(zipPath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value="/downloadDummy", produces="application/zip")
	public ResponseEntity<Resource> downloadDummy (){
		
		//https://stackoverflow.com/questions/27952949/spring-rest-create-zip-file-and-send-it-to-the-client
		

		log.info("download");
		
		File zipFile = Paths.get("C:\\upload", "example.zip").toFile();
		
		
		// org.springframework.core.io.Resource 을 통해서 zip파일 보내기
		Resource resource = new FileSystemResource(zipFile);
		
		String resourceName ="";
		try {
			resourceName = new String( resource.getFilename().getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.info(e.toString());
		}
		
		log.info("Resource: "+resourceName);
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition","attachment; filename="+resourceName);
		
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
}
