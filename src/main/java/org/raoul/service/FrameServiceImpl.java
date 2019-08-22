package org.raoul.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.raoul.domain.PhotoDTO;
import org.raoul.persistence.FrameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
public class FrameServiceImpl implements FrameService {

	@Autowired
	FrameMapper fMapper;
	
	@Override
	public File makeZipByUfid(String ufid) {
		
				//make zip entry
				log.info("make zip entry");
				
				List<PhotoDTO> listPhoto = null;
				List<Path> listPhotoPath = new ArrayList<Path>();
				List<Path> listTempPhotoPath = new ArrayList<Path>();
				listPhoto = fMapper.getLast20Photos(ufid);
				
				
				for(PhotoDTO dto:listPhoto) {
					listPhotoPath.add(Paths.get("C:","upload",dto.getFolderPath(),dto.getUuid()+'_'+dto.getOriginalPhotoName()));
					listTempPhotoPath.add(Paths.get("C;","upload","temp","tempPhoto",dto.getFolderPath(),dto.getUuid()+'_'+dto.getOriginalPhotoName()));
				}
				log.info(listPhotoPath.toString());
				
				for (int i = 0; i < listPhotoPath.size(); i++) {
					try {
						Files.copy(listPhotoPath.get(i), listTempPhotoPath.get(i));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// TODO 기존 파일을 임시폴더에 slide숫자.jpg로 저장.
				// 저장후 그 파일들로 압축파일 만들기. 
				
				//making zip file
				log.info("make zip");
				Path zipFolderPath = Paths.get("C:","upload","temp");
				Path zipPath = zipFolderPath.resolve(ufid+".zip");

				try (
					OutputStream fout = Files.newOutputStream(zipPath);
					ZipOutputStream	zos = new ZipOutputStream(fout);
				){
					listPhotoPath.stream().filter(path-> path.toFile().exists()).forEach((path)->{
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
		
		return zipFile;
	}


}
