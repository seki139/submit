/**
 * 
 */
 function validateForm() {
	
	//フォーム内のファイル入力フィールド（<input type="file">）を取得
    var fileInput = document.getElementById('file');
    //ファイル入力フィールドからファイルのパスを取得
    var filePath = fileInput.value;
    
    //ファイルが選択されていか
    if (filePath.trim() === '') {
        alert('ファイルを選択してください。');
        return false;
    }
    
    //許可されるファイルの拡張子を正規表現で定義
    var allowedExtensions = /(\.csv|\.txt)$/i;
    
    //ファイルパスが許可された拡張子のいずれかに属するか
    if (!allowedExtensions.exec(filePath)) {
        alert('CSVファイルまたはテキストファイルのみをアップロードできます。');
        fileInput.value = '';
        return false;
    }
    return true;
}
