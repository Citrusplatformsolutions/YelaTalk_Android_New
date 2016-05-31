package com.kainat.app.autocomplete.tag;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.kainat.app.android.R;

public class ChipsMultiAutoCompleteTextview extends MultiAutoCompleteTextView implements OnItemClickListener, TextView.OnEditorActionListener  {

	private final String TAG = "ChipsMultiAutoCompleteTextview";

	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context) {
		super(context);
		init(context);
	}
	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	/* set listeners for item click and text change */
	public void init(Context context){
		setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		setOnItemClickListener(this);
		addTextChangedListener(textWather);
		// Listen to IME action keys
		setOnEditorActionListener(this);
	}

	/*TextWatcher, If user type any country name and press comma then following code will regenerate chips */
	private TextWatcher textWather = new TextWatcher() {
		String oldtext;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			//	System.out.println("Count = "+count);
			//			if(count == 1){
//							((ChipsAdapter) getAdapter()).isTagCompletion(false);
			//			}
		/*	if(oldtext!=null && (oldtext.length()>s.toString().length())){
				if(s.toString().contains(" ")){
					int lastSpaceIndex = getText().toString().lastIndexOf(" ");
					String ass = getText().toString().substring(0, lastSpaceIndex);
					setText(ass);
					//setChips();
				}
			}*/
			
			if(s.toString().startsWith(" ")){
				setError("Sorry! char not Allowed");
				return;
			}
			try{
			if(count >=1 && s != null && s.toString().length() > 0 && !s.toString().startsWith(" ")){
				if(s.charAt(start) == ' '  && s.toString().endsWith(" ")){
					setText(s.toString().trim()+" ");
					setSelection(getText().toString().length());
				}
				typeChar = s;
				startCounter = start;
				
				if(s.charAt(start) == ' '){
					setChips(); // generate chips
				}else if(count == 15){
					setText(getText().toString()+" ");
					setChips();
				}else{
				}

			}
			else{
				if(s.length() < 3)
					Toast.makeText(getContext(), getContext().getString(R.string.please_enter_min_tag), Toast.LENGTH_SHORT).show();
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}

		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			oldtext = s.toString();
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	public int nCounter = 0;
	private CharSequence typeChar = null;
	private int startCounter = -1;
	@Override
	public boolean onEditorAction(TextView view, int action, KeyEvent keyEvent) {
		/*System.out.println("onEditorAction = "+view.getText().toString()+ "Action = "+action);
		if (action == EditorInfo.IME_ACTION_DONE) {
			String temp = getText().toString() +" ";
			setText(temp);
			setSelection(temp.length());
				if(typeChar != null && startCounter != -1)// &&  typeChar.charAt(startCounter) == ' ')
					setChips(); //
			return true;
		}*/
		return false;
	}
	int tempLenght = -1;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_TAB:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		case KeyEvent.KEYCODE_DEL:
			if(getText().toString() != null && !getText().toString().equals("") && getText().toString().contains(" ")){
				String tagArray[] = getText().toString().split(" ");
				if(tempLenght != tagArray.length){
					tempLenght = tagArray.length;
				}else{
					System.out.println("OnDelete Pressed = "+tagArray.length);
					nCounter -- ;
				}
				
			}else{
				nCounter = 0;
				tempLenght = -1;
			}
			break;
		}


		return handled || super.onKeyDown(keyCode, event);
	}// home. match, contact, repond to.
	/*This function has whole logic for chips generate*/
	public String setChips(){
		
		if(getText().toString().contains("# ")|| getText().toString().contains("$")){
			String str = getText().toString();
			str = str.replace("# ","");
			str = str.replace("$","");
			this.setText(str);
			setChips();
			return null;
		}
		
		//((ChipsAdapter) getAdapter()).isTagCompletion(true);
		if(getText().toString().contains(" ")) // check comman in string
		{

			SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
			// split string wich comma
			String chips[] = getText().toString().split(" ");
			int x =0;
			// loop will generate ImageSpan for every country name separated by comma
			for(String c : chips){
				if(c != null && c.length() > 15){
					c = c.substring(0, 15);
				}

				// inflate chips_edittext layout 
				LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				TextView textView = (TextView) lf.inflate(R.layout.chips_edittext, null);
				if(c.startsWith("#"))
					textView.setText(c); // set text
				else
					textView.setText("#"+c); // set text
				int image = ((ChipsAdapter) getAdapter()).getImage(c);
				textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, image, 0);
				// capture bitmapt of genreated textview
				int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				textView.measure(spec, spec);
				textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
				Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(),Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				canvas.translate(-textView.getScrollX(), -textView.getScrollY());
				textView.draw(canvas);
				textView.setDrawingCacheEnabled(true);
				Bitmap cacheBmp = textView.getDrawingCache();
				Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
				textView.destroyDrawingCache();  // destory drawable
				// create bitmap drawable for imagespan
				BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
				bmpDrawable.setBounds(0, 0,bmpDrawable.getIntrinsicWidth(),bmpDrawable.getIntrinsicHeight());
				// create and set imagespan 
				ssb.setSpan(new ImageSpan(bmpDrawable),x ,x + c.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				x = x+ c.length()+1;
			}
			// set chips span 
			setText(ssb);
			// move cursor to last 
			setSelection(getText().length());
			nCounter ++;
		}
		return null;

		

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ChipsItem ci = (ChipsItem) getAdapter().getItem(position);

		setChips(); // call generate chips when user select any item from auto complete
	}




}
