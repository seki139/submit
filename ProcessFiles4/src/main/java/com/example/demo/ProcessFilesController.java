package com.example.demo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class ProcessFilesController {

	
	//private FileProcessingService fileProcessingService;
	@Autowired
    private TextFileRepository textFileRepository;

    @Autowired
    private ImageFileRepository imageFileRepository;
    /**
     * upload.htmlに飛ぶ
     * @return
     */
	@RequestMapping("/upload")
	public String filesUpload() {
		return "upload";
	}
	/**
     * upload2.htmlに飛ぶ
     * @return
     */
	@RequestMapping("/upload2")
	public String filesUpload2() {
		return "upload2";
	}
	/**
     * /uploadTemplate.htmlに飛ぶ
     * @return
     */
	@RequestMapping("/uploadTemplate")
	public String uploadTemplate() {
		return "sqlUpload";
	}

	/**
	 * mysqlに任意のファイルを保存する
	 * @param form
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/uploadData")
	public String uploadData(@ModelAttribute UploadFormData form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) throws IOException{
		//htmlからデータを取得
				
				String password = form.getPassword();
				if ( password.equals("axqw1#p%n"))
					;
				else {
					return "error";
				}
				try {
					MultipartFile file = form.getFile();
		            String fileName = file.getOriginalFilename();
		            if (fileName == null) {
		                System.out.println("File name is null");
		                return "error";
		            }
		            byte[] content = file.getBytes();

		            if ( fileName.endsWith(".txt")|| fileName.toLowerCase().endsWith(".tex")) {
		                // テキストファイルの場合
		                TextFileEntity textFile = new TextFileEntity();
		                textFile.setName(fileName);
		                textFile.setContent(content);
		                textFileRepository.save(textFile);
		            } else if ( (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))) {
		                // 画像ファイルの場合
		                ImageFileEntity imageFile = new ImageFileEntity();
		                imageFile.setName(fileName);
		                imageFile.setContent(content);
		                imageFileRepository.save(imageFile);
		            } else {
		                return "error";
		            }

		            return "success";
		        } catch (IOException e) {
		        	e.printStackTrace();
		        	return "error";
		        }
		    }
	/**
	 * 1.sqlDownload.htmlに飛ぶ
	 * 2.mysqlに保存されてあるすべてのファイルを表示
	 * @param model
	 * @return
	 */
	@RequestMapping("/sqlDownload")
    public String listFiles(Model model) {
        model.addAttribute("textFiles", textFileRepository.findAll());
        model.addAttribute("imageFiles", imageFileRepository.findAll());
        return "sqlDownload"; // HTMLテンプレートの名前
    }
	/**
	 * ファイルの絞り込み(検索)
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping("/search")
    public String listFilesSearch(@ModelAttribute UploadFormData form,Model model) {
		String search = form.getSearch();
		// テキストファイルの検索
        List<TextFileEntity> textFiles = textFileRepository.searchByName(search);
        model.addAttribute("textFiles", textFiles);

        // 画像ファイルの検索
        List<ImageFileEntity> imageFiles = imageFileRepository.searchByName(search);
        model.addAttribute("imageFiles", imageFiles);
        return "sqlDownload"; // HTMLテンプレートの名前
    }
	/**
	 * idのテキストファイルをダウンロードさせる
	 * @param id
	 * @return
	 */
	@RequestMapping("/download/text/{id}")
	public ResponseEntity<byte[]> downloadTextFile(@PathVariable Long id) {
		TextFileEntity file = textFileRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("File not found"));
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.TEXT_PLAIN);
	    String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
	    headers.setContentDisposition(ContentDisposition.builder("attachment").filename(encodedFileName).build());
	    
	    return new ResponseEntity<>(file.getContent(), headers, HttpStatus.OK);
	}
	/**
	 * idの画像ファイルをダウンロードさせる
	 * @param id
	 * @return 
	 */
	@RequestMapping("/download/image/{id}")
	public ResponseEntity<byte[]> downloadImageFile(@PathVariable Long id) {
		ImageFileEntity file = imageFileRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("File not found"));
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
	    headers.setContentDisposition(ContentDisposition.builder("attachment").filename(encodedFileName).build());
	    
	    return new ResponseEntity<>(file.getContent(), headers, HttpStatus.OK);
    }
	/*
	private MediaType determineMediaType(String fileName) {
        if (fileName.toLowerCase().endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }*/
}
	/*
	//HttpServletRequest request
	@RequestMapping("/processFile")
	public String handleFileUpload(@ModelAttribute UploadFormData form, BindingResult bindingResult,
	        RedirectAttributes redirectAttributes) {
	    
	    List<MultipartFile> files = form.getFiles();
	    List<String> punctuation = form.getPunctuation();
	    String formatting = form.getFormatting(); 
	    
	    // サービスクラスを呼び出してファイルの処理を行う
	    List<MultipartFile> processedFiles = fileProcessingService.processFiles(files, punctuation, formatting,
	            bindingResult);
	    
	    if(processedFiles==null) {
	    	return "redirect:/error";
	    }
	    
	    redirectAttributes.addFlashAttribute("processedFiles", processedFiles);
	    
	    // 処理されたファイルをセッションに保存する
	    //WebUtils.setSessionAttribute(request, "processedFiles", processedFiles);
	    
	    // ファイル処理後のリダイレクト先を返す
	    return "redirect:/downloadProcessedFiles";
	}*/
