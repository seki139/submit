package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/")
public class ProcessFilesRestController {

	@Autowired
	private FileProcessingService fileProcessingService;
	/**
	 * ファイルの修正
	 * @param form
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/processFile")
	public ResponseEntity<Resource> handleFileUpload(@ModelAttribute UploadFormData form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		List<MultipartFile> files = form.getFiles();
		List<String> punctuation = form.getPunctuation();
		String formatting = form.getFormatting();

		if (files.size() > 1) {
			byte[] zipFileBytes = fileProcessingService.processFiles(files, punctuation, formatting, bindingResult);

			if (zipFileBytes != null) {
				HttpHeaders headers = new HttpHeaders();
				// 第一引数には attachment を指定しており、これはブラウザがファイルをダウンロードする際にダイアログを表示することを指示します
				// 第二引数はダウンロードされるファイルの名前を指定しています。
				headers.setContentDispositionFormData("attachment", "processed_files.zip");
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// ダウンロードするファイルのデータを表すリソースを作成
				ByteArrayResource resource = new ByteArrayResource(zipFileBytes);
				return ResponseEntity.ok().headers(headers).body(resource);
			} else {
				// Handle error condition
				return ResponseEntity.badRequest().body(null);
			}
		}
		else {
			List<byte[]> processedFiles = fileProcessingService.processFile(files, punctuation, formatting, bindingResult);

	        HttpHeaders headers = new HttpHeaders();
	        if (processedFiles.size() == 1) {
	        	String originalFileName = files.get(0).getOriginalFilename();
	            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
	            String fileName = "processed_file." + fileExtension;
	            //headers.setContentDispositionFormData("attachment", files.get(0).getOriginalFilename()); // Use original file name
	            headers.setContentDispositionFormData("attachment", fileName); 
	        } else {
	            headers.setContentDispositionFormData("inline", null);
	        }

	        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

	        ByteArrayResource resource = new ByteArrayResource(processedFiles.get(0)); // Use processed file bytes

	        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(resource);
		}

	}
	/**
	 * ファイルの修正２
	 * @param form
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/processFile2")
	public ResponseEntity<Resource> handleFileUpload2(@ModelAttribute UploadFormData form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		List<MultipartFile> files = form.getFiles();
		List<String> punctuation = form.getPunctuation();
		String formatting = form.getFormatting();

		if (files.size() > 1) {
			byte[] zipFileBytes = fileProcessingService.processFiles2(files, punctuation, formatting, bindingResult);

			if (zipFileBytes != null) {
				HttpHeaders headers = new HttpHeaders();
				// 第一引数には attachment を指定しており、これはブラウザがファイルをダウンロードする際にダイアログを表示することを指示します
				// 第二引数はダウンロードされるファイルの名前を指定しています。
				headers.setContentDispositionFormData("attachment", "processed_files.zip");
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// ダウンロードするファイルのデータを表すリソースを作成
				ByteArrayResource resource = new ByteArrayResource(zipFileBytes);
				return ResponseEntity.ok().headers(headers).body(resource);
			} else {
				// Handle error condition
				return ResponseEntity.badRequest().body(null);
			}
		}
		else {
			List<byte[]> processedFiles = fileProcessingService.processFile2(files, punctuation, formatting, bindingResult);

	        HttpHeaders headers = new HttpHeaders();
	        if (processedFiles.size() == 1) {
	        	String originalFileName = files.get(0).getOriginalFilename();
	        	//最後のドットのインデックスを取得(拡張子を探すため)
	            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
	            //拡張子をファイルに加える
	            String fileName = "processed_file." + fileExtension;
	            //headers.setContentDispositionFormData("attachment", files.get(0).getOriginalFilename()); // Use original file name
	            headers.setContentDispositionFormData("attachment", fileName); 
	        } else {
	            headers.setContentDispositionFormData("inline", null);
	        }

	        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

	        ByteArrayResource resource = new ByteArrayResource(processedFiles.get(0)); // Use processed file bytes

	        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(resource);
		}

	}
	
}
