package junkuvo.apps.meallogger.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class EllipseTextView extends RelativeLayout {
    private static final String TAG = EllipseTextView.class.getSimpleName();
    private final EllipseTextView self = this;

    Context mContext;
    RelativeLayout mRelativeLayout;
    TextView mTextView;

    public EllipseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EllipseTextView(Context context) {
        super(context);
        init(context);
    }

    public EllipseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        mContext = context;
        View view = (View) LayoutInflater.from(context).inflate(R.layout.textview_ellipse, this);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rllEllipse);
        mTextView = (TextView)mRelativeLayout.findViewById(R.id.txtMain);
    }

    public void setText(String text){
        mTextView.setText(text);
    }

    public CharSequence getText(){
        return mTextView.getText();
    }
}
