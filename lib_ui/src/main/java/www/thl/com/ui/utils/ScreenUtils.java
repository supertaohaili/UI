package www.thl.com.ui.utils;
import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.TextView;

public class ScreenUtils {


    public static int gettextwidth(Context context, String str, int fontsize) {
        TextView tv = new TextView(context);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        Paint paint = new Paint();
        paint = tv.getPaint();
        return (int) paint.measureText(str);
    }

    public static float getTextHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }
}