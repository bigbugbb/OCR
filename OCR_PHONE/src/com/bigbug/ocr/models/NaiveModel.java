package com.bigbug.ocr.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import weka.core.Instances;
import wlsvm.WLSVM;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.bigbug.ocr.R;

public class NaiveModel extends Model {
	
	private WLSVM mSVM;
	
	private static final String ARFF_PATH = "/sdcard/ocr.arff";

	public NaiveModel() {}
	
	public boolean isLoaded() {
		return mSVM != null;
	}

	@Override
	public boolean load() {
		boolean result = false;
		
		ObjectInputStream ois = null;
		try {
			InputStream is = mContext.getResources().openRawResource(R.raw.svm_model);
			ois = new ObjectInputStream(is);
			mSVM = (WLSVM) ois.readObject();			
			result = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (mListener != null) {
				mListener.onModelLoaded(result);
			}
		}
		
		return result;
	}

	@Override
	public char recognize(Bitmap input) {
		
		int w = input.getWidth();
		int h = input.getHeight();
		int[] pixels = new int[w * h];
		
		// get the character boundary
		input.getPixels(pixels, 0, w, 0, 0, w, h);
		Rect boundary = new Rect(0, 0, w, h);
		getBoundary(pixels, boundary);
		
		// get the region of interest from the boundary
		Bitmap roi = Bitmap.createBitmap(
			input, boundary.left, boundary.top, boundary.width(), boundary.height()
		);
		
		// create the bitmap for the overlay of the region of interest
		Bitmap overlay = createOverlayBitmap(roi);
		
		// overlay the region of interest
		Canvas canvas = new Canvas(overlay);
		int left = (overlay.getWidth() - roi.getWidth()) / 2;
		int top  = (overlay.getHeight() - roi.getHeight()) / 2;
        canvas.drawBitmap(roi, left, top, null);
        roi.recycle();
		
        // downscale the bitmap and rasterise it into 16 * 8 subblocks
        int[] binary = new int[128 + 16 + 8]; // 128 features and sum of 1 on each x and each y
        Bitmap downscaled = Bitmap.createScaledBitmap(overlay, (int) 128, (int) 256, true);
        overlay.recycle();
        rasteriseBitmap(downscaled, binary);
        downscaled.recycle();
        
        // add extra features by summing of 1 on each x and each y
        addExtraFeatures(binary);
				
		// write the data from binary to output arff file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(ARFF_PATH));
			String output = "@RELATION test";
			bw.write(output); bw.newLine();
			for(int i = 0; i < binary.length; ++i) {
				output ="@ATTRIBUTE " + i + " NUMERIC";
				bw.write(output); bw.newLine();
			}
			output = "@ATTRIBUTE label {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}";
			bw.write(output); bw.newLine();
			output = "@DATA"; 
			bw.write(output); bw.newLine();
			output = "";
			for (int i = 0; i < binary.length; ++i) {
				output += binary[i]; output += ",";
			}
			output += '?';			
			bw.write(output); bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		BufferedReader br = null;
		Instances realdata = null;
		try {
			br = new BufferedReader(new FileReader(ARFF_PATH));
			realdata = new Instances(br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		realdata.setClassIndex(realdata.numAttributes() - 1);
		try {
			double clsLabel = mSVM.classifyInstance(realdata.instance(0));
			String c = realdata.classAttribute().value((int) clsLabel);
			Log.i("HandwriteActivity", realdata.classAttribute().value((int) clsLabel));
			if (mListener != null) {
				mListener.onCharRecognized(c.charAt(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return 0; // TODO: change the value
	}

	private void getBoundary(int[] pixels, Rect boundary) {		
		// look for the boundary
		int w = boundary.width();
		int h = boundary.height();
		int l = w, r = 0, t = h, b = 0;
		int offset = 0, pos = 0;
		boolean found = false;
		for (int x = 0; x < boundary.width(); ++x) {
			if (found) break;
			offset = 0;
			for (int y = 0; y < h; ++y) {	
				pos = offset + x;
				if (pixels[pos] != 0) {					
					l = x;
					found = true;
					break;					
				}
				offset += w;
			}			
		}	
		found = false;
		for (int x = w - 1; x > 0; --x) {
			if (found) break;
			offset = 0;
			for (int y = 0; y < h; ++y) {
				pos = offset + x;
				if (pixels[pos] != 0) {					
					r = x;
					found = true;
					break;					
				}
				offset += w;
			}
		}
		found = false;
		offset = 0;
		for (int y = 0; y < h; ++y) {
			if (found) break;
			for (int x = 0; x < w; ++x) {
				pos = offset + x;
				if (pixels[pos] != 0) {
					t = y;
					found = true;
					break;
				}
			}
			offset += w;
		}
		found = false;
		offset = (h - 1) * w;
		for (int y = h - 1; y > 0; --y) {
			if (found) break;
			for (int x = 0; x < w; ++x) {
				pos = offset + x;
				if (pixels[pos] != 0) {
					b = y;
					found = true;
					break;
				}
			}
			offset -= w;
		}
		
		boundary.left   = l;
		boundary.right  = r;
		boundary.top    = t;
		boundary.bottom = b;
	}
	
	private Bitmap createOverlayBitmap(Bitmap roi) {
		
		int oldw = roi.getWidth();
		int oldh = roi.getHeight();

		int w, h;
		if (oldw >= oldh) {
			w = oldw;
			h = w << 1;
		} else {
			if (oldh / 2 >= oldw) {
				w = oldh / 2;
				h = oldh;
			} else {
				w = oldw;
				h = w << 1;
			}
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        Bitmap overlay = Bitmap.createBitmap(w, h, roi.getConfig());
        
        return overlay;
	}
	
	private void rasteriseBitmap(Bitmap bitmap, int[] binary) {		
		
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
		int[] pixels = new int[w * h];		
		int sw = w >> 3;
		int sh = h >> 4;				
		
		// rasterise the bitmap
		int k = 0;
		for (int y = 0; y < 16; ++y) {
			for (int x = 0; x < 8; ++x) {
				bitmap.getPixels(pixels, 0, w, x * sw, y * sh, sw, sh);
				for (int pixel : pixels) {
					if (pixel != 0) {
						binary[k] = 1;
						break;
					}
				}
				++k;
 			}
		}
	}
	
	private void addExtraFeatures(int[] features) {
		
		int k = 128;
		// sum of 1 on each x		
		for (int y = 0; y < 16; ++y) {
			int sum = 0;
			for (int x = 0; x < 8; ++x) {
				if (features[(y << 3) + x] == 1) {
					sum += 1;
				}
			}
			features[k++] = sum;
		}
		// sum of 1 on each y
		for (int x = 0; x < 8; ++x) {
			int sum = 0;
			for (int y = 0; y < 16; ++y) {
				if (features[(y << 3) + x] == 1) {
					sum += 1;
				}
			}
			features[k++] = sum;
		}		
	}
}
