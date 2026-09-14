import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VideoQrGenerator2 {
    public static void main(String[] args) {
        String targetUrl = "https://wangsx0.github.io/video-dq-demo/";
        String saveFilePath = "video-qrcode.png";
        String backgroundImgPath = "background.jpg";

        // 裁剪后的横幅尺寸，可根据需要调整
        int canvasWidth = 780;
        int canvasHeight = 720;

        // 二维码大小
        int qrSize = 500;

        // 顶部标题
        String titleText = "四哥台球炸清";
        int titleFontSize = 56;

        // 二维码四周留白
        int qrMargin = 10;

        try {
            File bgFile = new File(backgroundImgPath);
            if (!bgFile.exists()) {
                System.err.println("❌ 背景文件不存在：" + backgroundImgPath);
                return;
            }

            BufferedImage originalBg = ImageIO.read(bgFile);

            // 创建画布，先裁剪/缩放到指定横幅尺寸
            BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = canvas.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 按比例裁剪原图，居中铺满画布
            double scale = Math.max((double) canvasWidth / originalBg.getWidth(),
                    (double) canvasHeight / originalBg.getHeight());
            int scaledWidth = (int) (originalBg.getWidth() * scale);
            int scaledHeight = (int) (originalBg.getHeight() * scale);
            int offsetX = (canvasWidth - scaledWidth) / 2;
            int offsetY = (canvasHeight - scaledHeight) / 2;
            g2d.drawImage(originalBg, offsetX, offsetY, scaledWidth, scaledHeight, null);

            // 生成二维码
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(targetUrl, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

            // 标题绘制区域留出空间
            int titleAreaHeight = 140;

            // 标题加半透明黑底，增强可读性
            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.fillRect(0, 0, canvasWidth, titleAreaHeight);

            Font titleFont = new Font("SimHei", Font.BOLD, titleFontSize);
            g2d.setFont(titleFont);
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics(titleFont);
            int textWidth = fm.stringWidth(titleText);
            int textX = (canvasWidth - textWidth) / 2;
            int textY = (titleAreaHeight - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(titleText, textX, textY);

            // 二维码白色底板，避免贴背景
            int qrBoxSize = qrSize + qrMargin * 2;
            int boxX = (canvasWidth - qrBoxSize) / 2;
            int boxY = titleAreaHeight + (canvasHeight - titleAreaHeight - qrBoxSize) / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(boxX, boxY, qrBoxSize, qrBoxSize, 24, 24);

            // 二维码居中放在白色底板上
            int qrX = boxX + qrMargin;
            int qrY = boxY + qrMargin;
            g2d.drawImage(qrImage, qrX, qrY, qrSize, qrSize, null);

            // 底部留白，不再贴边
            int bottomPadding = 40;
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRect(0, canvasHeight - bottomPadding, canvasWidth, bottomPadding);

            g2d.dispose();

            ImageIO.write(canvas, "PNG", new File(saveFilePath));
            System.out.println("✅ 带背景+文字的二维码生成成功！文件：" + saveFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
