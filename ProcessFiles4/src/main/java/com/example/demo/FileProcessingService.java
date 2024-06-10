package com.example.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileProcessingService {
	/**
	 * 受け取ったファイルの修正
	 * @param files
	 * @param punctuation
	 * @param formatting
	 * @param bindingResult
	 * @return
	 */
	public byte[] processFiles(List<MultipartFile> files, List<String> punctuation, String formatting, BindingResult bindingResult) {
        try {
        	//zos に書き込まれたデータは、baos のバイト配列に書き込まれます
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName != null && !isTextOrCsvFile(fileName)) {
                    bindingResult.rejectValue("files", "invalid.file", "テキストファイルまたはCSVファイル以外のものは入れないでください");
                    return null;
                }

                //ByteArrayOutputStream fileBytes = new ByteArrayOutputStream();
                //データを読み取るための インスタンスを作成
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                //可変の文字列を効率的に扱うためのクラス
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replace("\r", ""); // Windows形式の改行を削除

                    // 空白を削除するか、文字や数字の書き始めの位置を揃える
                    if ("removeWhitespace".equals(formatting)) {
						line = line.replaceAll("\\s", "");
					}
					else if("removeTab".equals(formatting)) {
						line = line.replaceAll("\t", "");
					}
					else if("removeHead".equals(formatting)) {
						line = line.replaceAll("^\\s+", "");
					}
					else {
						;
					}

                    result.append(line).append("\n");
                }

                // 句読点や記号を削除する
                if (punctuation != null) {
					for (String p : punctuation) {
						// ピリオドとコンマを正規表現で除去する
						if(p.equals("comma")) {
							result = new StringBuilder(result.toString().replaceAll("[,]", ""));
						}
						else if(p.equals("period")) {
							result = new StringBuilder(result.toString().replaceAll("[.]", ""));
						}
						
						else if(p.equals("than")){
							result = new StringBuilder(result.toString().replaceAll("<", ""));
							result = new StringBuilder(result.toString().replaceAll(">", ""));
						}
						else if(p.equals("kagikkako")){
							result = new StringBuilder(result.toString().replaceAll("\\[", ""));
							result = new StringBuilder(result.toString().replaceAll("\\]", ""));
						
						}
						else if(p.equals("tyukkako")){
							result = new StringBuilder(result.toString().replaceAll("\\{", ""));
							result = new StringBuilder(result.toString().replaceAll("\\}", ""));
						
						}
						// result = new StringBuilder(result.toString().replace(p, ""));
						else {
							continue;
						}
					}
				}
                //文字列をバイト配列に変換
                byte[] processedFileBytes = result.toString().getBytes();
                //zip化
                ZipEntry zipEntry = new ZipEntry(fileName);
                //エントリーを作成
                zos.putNextEntry(zipEntry);
                //書き込み
                zos.write(processedFileBytes);
                //エントリーを閉じます
                zos.closeEntry();

                reader.close();
            }

            zos.close();
            //バイト配列として返す
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            // エラーハンドリングを行う
        }

        return null;
    }
	/**
	 * 受け取ったファイルの修正2
	 * @param files
	 * @param punctuation
	 * @param formatting
	 * @param bindingResult
	 * @return
	 */
	public List<byte[]> processFile(List<MultipartFile> files, List<String> punctuation, String formatting,
			BindingResult bindingResult) {
		List<byte[]> processedFiles = new ArrayList<>();

		for (MultipartFile file : files) {
			try {
				String fileName = file.getOriginalFilename();
				if (fileName != null && !isTextOrCsvFile(fileName)) {
					bindingResult.rejectValue("files", "invalid.file", "テキストファイルまたはCSVファイル以外のものは入れないでください");
					return null;
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					// 文章の末尾の改行は残す
					// !line.isEmpty()行が空でないことを確認
					// '\n'はjavaでは一文字分
					line = line.replace("\r", ""); // Windows形式の改行を削除
					// 空白を削除するか、文字や数字の書き始めの位置を揃える
					if ("removeWhitespace".equals(formatting)) {
						line = line.replaceAll("\\s", "");
					}
					else if("removeTab".equals(formatting)) {
						line = line.replaceAll("\t", "");
					}
					else if("removeHead".equals(formatting)) {
						line = line.replaceAll("^\\s+", "");
					}
					else {
						;
					}
					result.append(line).append("\n");

					/*
					 * if (!line.isEmpty() && line.charAt(line.length() - 1) == '\n') {
					 * result.append(line); result.append("\r\n"); } else { result.append(line); }
					 */
					// result.append(line);
				}

				// 句読点や記号を削除する
				if (punctuation != null) {
					for (String p : punctuation) {
						// ピリオドとコンマを正規表現で除去する
						if(p.equals("comma")) {
							result = new StringBuilder(result.toString().replaceAll("[,]", ""));
						}
						else if(p.equals("period")) {
							result = new StringBuilder(result.toString().replaceAll("[.]", ""));
						}
						
						else if(p.equals("than")){
							result = new StringBuilder(result.toString().replaceAll("<", ""));
							result = new StringBuilder(result.toString().replaceAll(">", ""));
						}
						else if(p.equals("kagikkako")){
							result = new StringBuilder(result.toString().replaceAll("\\[", ""));
							result = new StringBuilder(result.toString().replaceAll("\\]", ""));
						
						}
						else if(p.equals("tyukkako")){
							result = new StringBuilder(result.toString().replaceAll("\\{", ""));
							result = new StringBuilder(result.toString().replaceAll("\\}", ""));
						
						}
						// result = new StringBuilder(result.toString().replace(p, ""));
						else {
							continue;
						}
					}
				}
				processedFiles.add(result.toString().getBytes()); // Convert processed text to bytes and add to the list
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				// エラーハンドリングを行う
			}
		}
		return processedFiles;
	}
	/**
	 * 受け取ったファイルの修正3
	 * @param files
	 * @param punctuation
	 * @param formatting
	 * @param bindingResult
	 * @return
	 */
	public byte[] processFiles2(List<MultipartFile> files, List<String> punctuation, String formatting, BindingResult bindingResult) {
        try {
        	//zos に書き込まれたデータは、baos のバイト配列に書き込まれます
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName != null && !isTextOrCsvFile2(fileName)) {
                    bindingResult.rejectValue("files", "invalid.file", "テキストファイルまたはtexファイル以外のものは入れないでください");
                    return null;
                }

                //ByteArrayOutputStream fileBytes = new ByteArrayOutputStream();
                //データを読み取るための インスタンスを作成
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                //可変の文字列を効率的に扱うためのクラス
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replace("\r", ""); // Windows形式の改行を削除
                 // 空白を削除するか、文字や数字の書き始めの位置を揃える
					if ("removeWhitespace".equals(formatting)) {
						line = line.replaceAll("\\s", "");
					}
					else if("removeTab".equals(formatting)) {
						line = line.replaceAll("\t", "");
					}
					else if("removeHead".equals(formatting)) {
						line = line.replaceAll("^\\s+", "");
					}
					else if("removeOther".equals(formatting)) {
						line = line.replaceAll("(?<=\\S)\\s+", "");
					}
					else {
						;
					}

                    result.append(line).append("\n");
                }

                
                if (punctuation != null) {
					for (String p : punctuation) {
						// 全角から半角
						if (p.equals("sign")) {
							result = new StringBuilder(result.toString().replaceAll("、", ","));
							result = new StringBuilder(result.toString().replaceAll("。", "."));
						} else if (p.equals("number")) {
							result = new StringBuilder(convertToHalfWidthNumbers(result.toString()));
						}
						// result = new StringBuilder(result.toString().replace(p, ""));
						else {
							continue;
						}
					}
				}
                //文字列をバイト配列に変換
                byte[] processedFileBytes = result.toString().getBytes();
                //zip化
                ZipEntry zipEntry = new ZipEntry(fileName);
                //エントリーを作成
                zos.putNextEntry(zipEntry);
                //書き込み
                zos.write(processedFileBytes);
                //エントリーを閉じます
                zos.closeEntry();

                reader.close();
            }

            zos.close();
            //バイト配列として返す
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            // エラーハンドリングを行う
        }

        return null;
    }
	/**
	 * 受け取ったファイルの修正4
	 * @param files
	 * @param punctuation
	 * @param formatting
	 * @param bindingResult
	 * @return
	 */
	public List<byte[]> processFile2(List<MultipartFile> files, List<String> punctuation, String formatting,
			BindingResult bindingResult) {
		List<byte[]> processedFiles = new ArrayList<>();

		for (MultipartFile file : files) {
			try {
				String fileName = file.getOriginalFilename();
				if (fileName != null && !isTextOrCsvFile2(fileName)) {
					bindingResult.rejectValue("files", "invalid.file", "テキストファイルまたはtexファイル以外のものは入れないでください");
					return null;
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					// 文章の末尾の改行は残す
					// !line.isEmpty()行が空でないことを確認
					// '\n'はjavaでは一文字分
					line = line.replace("\r", ""); // Windows形式の改行を削除
					// 空白を削除するか、文字や数字の書き始めの位置を揃える
					if ("removeWhitespace".equals(formatting)) {
						line = line.replaceAll("\\s", "");
					}
					else if("removeTab".equals(formatting)) {
						line = line.replaceAll("\t", "");
					}
					else if("removeHead".equals(formatting)) {
						line = line.replaceAll("^\\s+", "");
					}
					else if("removeOther".equals(formatting)) {
						line = line.replaceAll("(?<=\\S)\\s+", "");
					}
					else {
						;
					}
					result.append(line).append("\n");

					/*
					 * if (!line.isEmpty() && line.charAt(line.length() - 1) == '\n') {
					 * result.append(line); result.append("\r\n"); } else { result.append(line); }
					 */
					// result.append(line);
				}

				
				if (punctuation != null) {
					for (String p : punctuation) {
						// 全角から半角
						if (p.equals("sign")) {
							result = new StringBuilder(result.toString().replaceAll("、", ","));
							result = new StringBuilder(result.toString().replaceAll("。", "."));
						} else if (p.equals("number")) {
							result = new StringBuilder(convertToHalfWidthNumbers(result.toString()));
						}
						// result = new StringBuilder(result.toString().replace(p, ""));
						else {
							continue;
						}
					}
				}

				processedFiles.add(result.toString().getBytes()); // Convert processed text to bytes and add to the list
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				// エラーハンドリングを行う
			}
		}
		return processedFiles;
	}
	
	
	/**
	 * 受け取るファイルの指定
	 * @param fileName
	 * @return
	 */
	private boolean isTextOrCsvFile(String fileName) {
		return fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".csv");
	}
	/**
	 * 受け取るファイルの指定2
	 * @param fileName
	 * @return
	 */
	private boolean isTextOrCsvFile2(String fileName) {
		return fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".tex");
	}

	 
	/**
	 * 全角数字を半角数字に変換するメソッド
	 * @param input
	 * @return
	 */
	private String convertToHalfWidthNumbers(String input) {
	    // 全角数字と対応する半角数字のマッピング
	    String[] fullWidthNumbers = {"０", "１", "２", "３", "４", "５", "６", "７", "８", "９"};
	    String[] halfWidthNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	    // 全角数字を半角数字に置換する
	    for (int i = 0; i < fullWidthNumbers.length; i++) {
	        input = input.replace(fullWidthNumbers[i], halfWidthNumbers[i]);
	    }

	    return input;
	}
}
