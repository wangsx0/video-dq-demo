import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class VideoQrGenerator {
    public static void main(String[] args) {
        // 二维码跳转地址（你的视频网页）
        String targetUrl = "https://wangsx0.github.io/video-dq-demo/";
        // 生成二维码图片保存路径
        String saveFilePath = "video-qrcode0.png";

        int width = 500;
        int height = 500;

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 高容错，二维码轻微破损仍可识别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 白边margin
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(targetUrl, BarcodeFormat.QR_CODE, width, height, hints);

            Path path = Paths.get(saveFilePath);
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            System.out.println("✅二维码生成成功！文件：" + saveFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
