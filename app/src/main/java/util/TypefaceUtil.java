package util;
import android.content.Context;
import android.graphics.Typeface;

public class TypefaceUtil {
 
    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     * @param context to work with assets

     */
    public static Typeface getMyFont(Context context) {
    	 Typeface customFontTypeface = null;
    	try {
              customFontTypeface = Typeface.createFromAsset(context.getAssets(),"GothamMedium.ttf");
 
        } catch (Exception ignored) {
        }
        return customFontTypeface;
    }
    

}