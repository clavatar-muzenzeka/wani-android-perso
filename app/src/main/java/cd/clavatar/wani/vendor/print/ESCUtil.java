package cd.clavatar.wani.vendor.print;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import cd.clavatar.wani.data.model.CompactPaiement;

public class ESCUtil {

	public static final byte ESC = 27;// æ�¢ç �
	public static final byte FS = 28;// æ–‡æœ¬åˆ†éš”ç¬¦
	public static final byte GS = 29;// ç»„åˆ†éš”ç¬¦
	public static final byte DLE = 16;// æ•°æ�®è¿žæŽ¥æ�¢ç �
	public static final byte EOT = 4;// ä¼ è¾“ç»“æ�Ÿ
	public static final byte ENQ = 5;// è¯¢é—®å­—ç¬¦
	public static final byte SP = 32;// ç©ºæ ¼
	public static final byte HT = 9;// æ¨ªå�‘åˆ—è¡¨
	public static final byte LF = 10;// æ‰“å�°å¹¶æ�¢è¡Œï¼ˆæ°´å¹³å®šä½�ï¼‰
	public static final byte CR = 13;// å½’ä½�é”®
	public static final byte FF = 12;// èµ°çº¸æŽ§åˆ¶ï¼ˆæ‰“å�°å¹¶å›žåˆ°æ ‡å‡†æ¨¡å¼�ï¼ˆåœ¨é¡µæ¨¡å¼�ä¸‹ï¼‰ ï¼‰
	public static final byte CAN = 24;// ä½œåºŸï¼ˆé¡µæ¨¡å¼�ä¸‹å�–æ¶ˆæ‰“å�°æ•°æ�® ï¼‰

	// ------------------------æ‰“å�°æœºåˆ�å§‹åŒ–-----------------------------

	/**
	 * æ‰“å�°æœºåˆ�å§‹åŒ–
	 * 
	 * @return
	 */
	public static byte[] init_printer() {
		byte[] result = new byte[2];
		result[0] = ESC;
		result[1] = 64;
		return result;
	}

	// ------------------------æ�¢è¡Œ-----------------------------

	/**
	 * æ�¢è¡Œ
	 * 
	 * @param ¦�æ�¢å‡ è¡Œ
	 * @return
	 */
	public static byte[] nextLine(int lineNum) {
		byte[] result = new byte[lineNum];
		for (int i = 0; i < lineNum; i++) {
			result[i] = LF;
		}

		return result;
	}

	// ------------------------ä¸‹åˆ’çº¿-----------------------------

	/**
	 * ç»˜åˆ¶ä¸‹åˆ’çº¿ï¼ˆ1ç‚¹å®½ï¼‰
	 * 
	 * @return
	 */
	public static byte[] underlineWithOneDotWidthOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 1;
		return result;
	}

	/**
	 * ç»˜åˆ¶ä¸‹åˆ’çº¿ï¼ˆ2ç‚¹å®½ï¼‰
	 * 
	 * @return
	 */
	public static byte[] underlineWithTwoDotWidthOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 2;
		return result;
	}

	/**
	 * å�–æ¶ˆç»˜åˆ¶ä¸‹åˆ’çº¿
	 * 
	 * @return
	 */
	public static byte[] underlineOff() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 0;
		return result;
	}

	// ------------------------åŠ ç²—-----------------------------

	/**
	 * é€‰æ‹©åŠ ç²—æ¨¡å¼�
	 * 
	 * @return
	 */
	public static byte[] boldOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 69;
		result[2] = 0xF;
		return result;
	}

	/**
	 * å�–æ¶ˆåŠ ç²—æ¨¡å¼�
	 * 
	 * @return
	 */
	public static byte[] boldOff() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 69;
		result[2] = 0;
		return result;
	}

	// ------------------------å¯¹é½�-----------------------------

	/**
	 * å·¦å¯¹é½�
	 * 
	 * @return
	 */
	public static byte[] alignLeft() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 0;
		return result;
	}

	/**
	 * å±…ä¸­å¯¹é½�
	 * 
	 * @return
	 */
	public static byte[] alignCenter() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 1;
		return result;
	}

	/**
	 * å�³å¯¹é½�
	 * 
	 * @return
	 */
	public static byte[] alignRight() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 2;
		return result;
	}

	/**
	 * æ°´å¹³æ–¹å�‘å�‘å�³ç§»åŠ¨colåˆ—
	 * 
	 * @param col
	 * @return
	 */
	public static byte[] set_HT_position(byte col) {
		byte[] result = new byte[4];
		result[0] = ESC;
		result[1] = 68;
		result[2] = col;
		result[3] = 0;
		return result;
	}
	// ------------------------å­—ä½“å�˜å¤§-----------------------------

	/**
	 * å­—ä½“å�˜å¤§ä¸ºæ ‡å‡†çš„nå€�
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] fontSizeSetBig(int num) {
		byte realSize = 0;
		switch (num) {
		case 1:
			realSize = 0;
			break;
		case 2:
			realSize = 17;
			break;
		case 3:
			realSize = 34;
			break;
		case 4:
			realSize = 51;
			break;
		case 5:
			realSize = 68;
			break;
		case 6:
			realSize = 85;
			break;
		case 7:
			realSize = 102;
			break;
		case 8:
			realSize = 119;
			break;
		}
		byte[] result = new byte[3];
		result[0] = 29;
		result[1] = 33;
		result[2] = realSize;
		return result;
	}

	// ------------------------å­—ä½“å�˜å°�-----------------------------

	/**
	 * å­—ä½“å�–æ¶ˆå€�å®½å€�é«˜
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] fontSizeSetSmall(int num) {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 33;

		return result;
	}

	// ------------------------åˆ‡çº¸-----------------------------

	/**
	 * è¿›çº¸å¹¶å…¨éƒ¨åˆ‡å‰²
	 * 
	 * @return
	 */
	public static byte[] feedPaperCutAll() {
		byte[] result = new byte[4];
		result[0] = GS;
		result[1] = 86;
		result[2] = 65;
		result[3] = 0;
		return result;
	}

	/**
	 * è¿›çº¸å¹¶åˆ‡å‰²ï¼ˆå·¦è¾¹ç•™ä¸€ç‚¹ä¸�åˆ‡ï¼‰
	 * 
	 * @return
	 */
	public static byte[] feedPaperCutPartial() {
		byte[] result = new byte[4];
		result[0] = GS;
		result[1] = 86;
		result[2] = 66;
		result[3] = 0;
		return result;
	}

	// ------------------------åˆ‡çº¸-----------------------------
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	public static byte[] byteMerger(byte[][] byteList) {

		int length = 0;
		for (int i = 0; i < byteList.length; i++) {
			length += byteList[i].length;
		}
		byte[] result = new byte[length];

		int index = 0;
		for (int i = 0; i < byteList.length; i++) {
			byte[] nowByte = byteList[i];
			for (int k = 0; k < byteList[i].length; k++) {
				result[index] = nowByte[k];
				index++;
			}
		}
		for (int i = 0; i < index; i++) {
			// CommonUtils.LogWuwei("", "result[" + i + "] is " + result[i]);
		}
		return result;
	}

	// --------------------
	public static byte[] generateMockData(String txt) {
		try {
			byte[] title = "".getBytes("gb2312");
			byte[] next2Line = ESCUtil.nextLine(1);
			byte[] boldOn = ESCUtil.boldOn();
			byte[] fontSize2Big = ESCUtil.fontSizeSetSmall(3);
			byte[] Focus = txt.getBytes("gb2312");
			byte[] boldOff = ESCUtil.boldOff();
			byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
			byte[] next1Line = ESCUtil.nextLine(1);
			
			byte[] breakPartial = ESCUtil.feedPaperCutPartial();

			byte[][] cmdBytes = { title, next2Line, boldOn, fontSize2Big, Focus, boldOff, fontSize2Small, next1Line,
								breakPartial };

			return ESCUtil.byteMerger(cmdBytes);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getPrintQRCode(String code, int modulesize, int errorlevel){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			buffer.write(setQRCodeSize(modulesize));
			buffer.write(setQRCodeErrorLevel(errorlevel));
			buffer.write(getQCodeBytes(code));
			buffer.write(getBytesForPrintQRCode(true));
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	public static byte[] getPrintDoubleQRCode(String code1, String code2, int modulesize, int errorlevel){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			buffer.write(setQRCodeSize(modulesize));
			buffer.write(setQRCodeErrorLevel(errorlevel));
			buffer.write(getQCodeBytes(code1));
			buffer.write(getBytesForPrintQRCode(false));
			buffer.write(getQCodeBytes(code2));

			//加入横向间隔
			buffer.write(new byte[]{0x1B, 0x5C, 0x18, 0x00});

			buffer.write(getBytesForPrintQRCode(true));
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}


	private static byte[] getBytesForPrintQRCode(boolean single){
		//打印已存入数据的二维码
		byte[] dtmp;
		if(single){		//同一行只打印一个QRCode， 后面加换行
			dtmp = new byte[9];
			dtmp[8] = 0x0A;
		}else{
			dtmp = new byte[8];
		}
		dtmp[0] = 0x1D;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x51;
		dtmp[7] = 0x30;
		return dtmp;
	}


	private static byte[] getQCodeBytes(String code) {
		//二维码存入指令
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			byte[] d = code.getBytes("GB18030");
			int len = d.length + 3;
			if (len > 7092) len = 7092;
			buffer.write((byte) 0x1D);
			buffer.write((byte) 0x28);
			buffer.write((byte) 0x6B);
			buffer.write((byte) len);
			buffer.write((byte) (len >> 8));
			buffer.write((byte) 0x31);
			buffer.write((byte) 0x50);
			buffer.write((byte) 0x30);
			for (int i = 0; i < d.length && i < len; i++) {
				buffer.write(d[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	private static byte[] setQRCodeErrorLevel(int errorlevel){
		//二维码纠错等级设置指令
		byte[] dtmp = new byte[8];
		dtmp[0] = GS;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x45;
		dtmp[7] = (byte)(48+errorlevel);
		return dtmp;
	}

	private static byte[] setQRCodeSize(int modulesize){
		//二维码块大小设置指令
		byte[] dtmp = new byte[8];
		dtmp[0] = GS;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x43;
		dtmp[7] = (byte)modulesize;
		return dtmp;
	}



	public static byte[] generatePaiementData(CompactPaiement paiement) {
		try {
			byte[] titleMin="MINISTERE DE L".getBytes("gb2312");
			byte[] boldOn = ESCUtil.boldOn();
			byte[] titleSerH="MINISTERE DE L".getBytes("gb2312");

			byte[][] cmdBytes = { titleMin, boldOn, titleSerH };
			return ESCUtil.byteMerger(cmdBytes);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}