package sketch;
import java.io.*;
import java.util.*;
import com.csvreader.CsvWriter;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class project{

	static class gesture {
	
	public String id;
	
	public long time;
	
	public String interpretation;
	
	public int confidence;
	
	
	static class stroke{
		public ArrayList<stroke_point> stroke_points = new ArrayList<stroke_point>();
		
	}
	
	public class domain{
		String _0;
		String _1;
	}
	
	static class stroke_point{
		String id;
	}
	
	static class point{
		long x;
		long y;
		long time;
		String id;
	}
	
	/*public static class shapes{
		String interpretation;
		int confidence;		
	}*/
	
	public ArrayList<stroke> ls = new ArrayList<stroke>(); 
	public ArrayList<point> actual_points = new ArrayList<point>();
	public ArrayList<point> sampled_points = new ArrayList<point>();
	
	static ArrayList<point> sampling(ArrayList<point> list){
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>(); 
		ArrayList<point> ans = new ArrayList<point>();		
		for (int i=0;i<list.size();i++)
		{
			temp_x.add(list.get(i).x);
			temp_y.add(list.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		double diag_length = Math.sqrt( Math.pow((x_max - x_min),2.0) + Math.pow((y_max - y_min),2.0));
		double sampling_distance = diag_length/40.0;
		
		
		double D = 0.0;
		point q = new point();
		ans.add(list.get(0));
		for(int i=1;i<list.size()-1;i++)
		{
			double d = Math.sqrt( Math.pow((list.get(i).x - list.get(i-1).x),2.0) + Math.pow((list.get(i).y - list.get(i-1).y),2.0));
			if((d+D)>= sampling_distance)
			{
				q.x = (long) (list.get(i-1).x + (sampling_distance-D)*(list.get(i).x - list.get(i-1).x) /d); 
				q.y = (long) (list.get(i-1).y + (sampling_distance-D)*(list.get(i).y - list.get(i-1).y) /d);
				ans.add(q);
				D=0;
			}
			else
				D=D+d;
			
		}
		
		/*double D =0.0;
		point q = new point();
		q = list.get(0);
		ans.add(q);
		for(int i=1;i<list.size()-1;i++)
		{
			point p = new point();
			p = list.get(i);
			double dist = Math.sqrt( Math.pow((q.x - p.x),2.0) + Math.pow((q.y - p.y),2.0));
			if(dist >= sampling_distance)
			{
				point r = new point();
				r=p;
				r.x = (long) (q.x + (p.x- q.x)*sampling_distance/dist);
				r.y = (long) (q.y + (p.y- q.y)*sampling_distance/dist);
				ans.add(r);
				q=r;			
			}
		}*/
		return ans;
		
	}

}

	static class features {
		
	static double sine_starting(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double ans =0.0;
		double x_0 = ll.get(0).x;
		double x_1 = ll.get(2).x;
		double y_0 = ll.get(0).y;
		double y_1 = ll.get(2).y;
		ans = (y_1 - y_0) / Math.sqrt(Math.pow(y_1 - y_0, 2) + Math.pow(x_1 - x_0, 2));
		return ans;
	}
	
	static double cosine_starting(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double ans =0.0;
		double x_0 = ll.get(0).x;
		double x_1 = ll.get(2).x;
		double y_0 = ll.get(0).y;
		double y_1 = ll.get(2).y;
		ans = (x_1 - x_0) / Math.sqrt(Math.pow(y_1 - y_0, 2) + Math.pow(x_1 - x_0, 2));
		return ans;
	}
	
	static double boudingBox_angle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return Math.toDegrees(Math.atan((y_max-y_min)/(x_max-x_min+0.001)));
	}
	
	static double boudingBox_diagonal(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		
		
		return Math.sqrt(Math.pow(y_max-y_min, 2.0) + Math.pow(x_max - x_min, 2.0));
	}
	
	static double endPointDistance(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double x_first = ll.get(0).x;
		double x_last = ll.get(ll.size()-1).x;
		double y_first = ll.get(0).y;
		double y_last = ll.get(ll.size()-1).y;		
		return Math.sqrt(Math.pow(y_last-y_first, 2.0) + Math.pow(x_last - x_first, 2.0));
	}
	
	static double cosineendPoint(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double x_first = ll.get(0).x;
		double x_last = ll.get(ll.size()-1).x;
		double y_first = ll.get(0).y;
		double y_last = ll.get(ll.size()-1).y;		
		return (x_last-x_first)/Math.sqrt(Math.pow(y_last-y_first, 2.0) + Math.pow(x_last - x_first, 2.0));
	}
	
	static double sineendPoint(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double x_first = ll.get(0).x;
		double x_last = ll.get(ll.size()-1).x;
		double y_first = ll.get(0).y;
		double y_last = ll.get(ll.size()-1).y;		
		return (y_last-y_first)/Math.sqrt(Math.pow(y_last-y_first, 2.0) + Math.pow(x_last - x_first, 2.0));
	}
	
	static double totalGestureLength(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double length=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			length += Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2));
			//System.out.println(Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2)));
		}
		return length;
	}
	
	static double totalStrokesLength(project.gesture g){
		ArrayList<gesture.stroke> ll = new ArrayList<gesture.stroke>();
		ll = g.ls;
		ArrayList<gesture.point> pp = new ArrayList<gesture.point>();
		pp = g.actual_points;
		double maxLength = 0.0;
		double totalLength = 0.0;
		int k=0;
		int max =0;
		for(int i=0;i<ll.size();i++)
		{
			double strokeLength = 0.0;
			for(int j=0;j<ll.get(i).stroke_points.size()-1;j++)
			{
				
				strokeLength = strokeLength + Math.sqrt(Math.pow(pp.get(k).x - pp.get(k+1).x , 2) + Math.pow(pp.get(k).y - pp.get(k+1).y , 2)); 
				if(strokeLength > maxLength)
				{
					maxLength = strokeLength;
					max = i;
				}
				k++;
			}
			totalLength = totalLength + strokeLength;
		}
		return totalLength;	
	}
	
	static double maxStrokeToTotalStrokesLengthRatio(project.gesture g){
		ArrayList<gesture.stroke> ll = new ArrayList<gesture.stroke>();
		ll = g.ls;
		ArrayList<gesture.point> pp = new ArrayList<gesture.point>();
		pp = g.actual_points;
		double maxLength = 0.0;
		double totalLength = 0.0;
		int k=0;
		int max =0;
		for(int i=0;i<ll.size();i++)
		{
			double strokeLength = 0.0;
			for(int j=0;j<ll.get(i).stroke_points.size()-1;j++)
			{
				
				strokeLength = strokeLength + Math.sqrt(Math.pow(pp.get(k).x - pp.get(k+1).x , 2) + Math.pow(pp.get(k).y - pp.get(k+1).y , 2)); 
				if(strokeLength > maxLength)
				{
					maxLength = strokeLength;
					max = i;
				}
				k++;
			}
			totalLength = totalLength + strokeLength;
		}
		return maxLength/totalLength;	
	}	
	
	static double totalAngleTraversed(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )) ;
		}
		return angle;
	}
	
	static double totalAbsoluteAngle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.abs(Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  ))) ;
		}
		return angle;
	}
	
	static double totalSquaredAngle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.pow(Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )),2) ;
		}
		return angle;
	}
	
	static double aspectAngle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return Math.abs(45 -Math.toDegrees(Math.atan((y_max-y_min)/(x_max-x_min+0.001))));	
	}
	
	static double curviness(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			double ang = Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )); 
			if(Math.abs(ang)<19)
				angle = angle + Math.abs(ang);
		}
		return angle;
	}
	
	static double totalAngleTraversedOverTotalLength(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )) ;
		}
		double length=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			length += Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2));
		}
		return angle/length;
	}
	
	static double densityMatrixOne(ArrayList <project.gesture.point> ll){	
		if(ll.size()==0)
			return 0.0;
		double length=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			length += Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2));
		}
		double x_first = ll.get(0).x;
		double x_last = ll.get(ll.size()-1).x;
		double y_first = ll.get(0).y;
		double y_last = ll.get(ll.size()-1).y;		
		return length/(Math.sqrt(Math.pow(y_last-y_first, 2.0) + Math.pow(x_last - x_first, 2.0)));	
	}
	
	static double densityMatrixTwo(ArrayList <project.gesture.point> ll){	
		double length=0.0;
		if(ll.size()==0)
			return 0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			length += Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2));
		}
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return length/Math.sqrt(Math.pow(y_max-y_min, 2.0) + Math.pow(x_max - x_min, 2.0));	
	}
	
	static double openness(ArrayList <project.gesture.point> ll){	
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		double boundingBox = Math.sqrt(Math.pow(y_max-y_min, 2.0) + Math.pow(x_max - x_min, 2.0));
		
		double x_first = ll.get(0).x;
		double x_last = ll.get(ll.size()-1).x;
		double y_first = ll.get(0).y;
		double y_last = ll.get(ll.size()-1).y;
		return (Math.sqrt(Math.pow(y_last-y_first, 2.0) + Math.pow(x_last - x_first, 2.0)))/boundingBox;	
	}	
	
	static double areaBoundingBox(ArrayList <project.gesture.point> ll){	
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return (y_max-y_min)*(x_max - x_min);			
	}
	
	static double logAreaBoundingBox(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return Math.log10(((y_max-y_min)*(x_max - x_min)));			
	}
	
	static double totalAngleOverTotalAbsoluteAngle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle = 0.0;
		double absAngle = 0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )) ;
			absAngle += Math.abs(Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001))));
		}
		return angle/absAngle;
	}
	
	static double logTotalLength(ArrayList <project.gesture.point> ll){	
		if(ll.size()==0)
			return 0.0;
		double length=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			length += Math.sqrt(Math.pow(ll.get(i).x-ll.get(i+1).x,2) + Math.pow(ll.get(i).y-ll.get(i+1).y,2));
		}
		return Math.log10(length);
	}
	
	static double logAspectAngle(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		return Math.log10(Math.abs(45 -Math.toDegrees(Math.atan((y_max-y_min)/(x_max-x_min + 0.001)))));	
	}
	
	static double shaftLengthToTotalLengthRatio(project.gesture g){
		ArrayList<gesture.stroke> ll = new ArrayList<gesture.stroke>();
		ll = g.ls;
		if(ll.size()==0)
			return 0.0;
		ArrayList<gesture.point> pp = new ArrayList<gesture.point>();
		pp = g.actual_points;
		double maxLength = 0.0;
		double totalLength = 0.0;
		int k=0;
		int max =0;
		for(int i=0;i<ll.size();i++)
		{
			double strokeLength = 0.0;
			for(int j=0;j<ll.get(i).stroke_points.size()-1;j++)
			{
				
				strokeLength = strokeLength + Math.sqrt(Math.pow(pp.get(k).x - pp.get(k+1).x , 2) + Math.pow(pp.get(k).y - pp.get(k+1).y , 2)); 
				if(strokeLength > maxLength)
				{
					maxLength = strokeLength;
					max = i;
				}
				k++;
			}
			totalLength = totalLength + strokeLength;
		}
		return maxLength/totalLength;	
	}
	
	static int directionChanges(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0;
		int n=0;
		for(int i=0;i<ll.size()-1;i++)
		{
			double ang = Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )); 
			if(Math.abs(ang)>50)
				n++;
		}
		return n;
	}
	
	static double averageCurvature(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )) ;
		}
		return angle/ll.size();
	}
	
	static double overlap(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		double angle=0.0;
		for(int i=0;i<ll.size()-1;i++)
		{
			angle += Math.toDegrees(Math.atan((ll.get(i+1).y - ll.get(i).y) /(ll.get(i+1).x - ll.get(i).x + 0.001)  )) ;
		}
		return angle/360;
	}
	
	static double thinness(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0.0;
		Long x_min ,x_max,  y_min,y_max;
		ArrayList<Long> temp_x = new ArrayList<Long>(); 
		ArrayList<Long> temp_y = new ArrayList<Long>();
		for (int i=0;i<ll.size();i++)
		{
			temp_x.add(ll.get(i).x);
			temp_y.add(ll.get(i).y);
		}
		x_min = (long) Collections.min(temp_x);
		y_min = (long) Collections.min(temp_y);
		x_max = (long) Collections.max(temp_x);
		y_max = (long) Collections.max(temp_y);
		double len = y_max - y_min;
		double wid = x_max - x_min;
		return 2*(len+wid)/len*wid;
	}
	
	static int densityNearEndPoints(ArrayList <project.gesture.point> ll){
		if(ll.size()==0)
			return 0;
		//Long x_min = (long) (2^13);
		Long x_max = (long) (-2^13);
		//Long y_min = (long) (2^13);
		Long y_max = (long) (-2^13);
		int idx_x_max=0,idx_x_min=0,idx_y_max=0,idx_y_min=0;
		int n=0;
		for (int i=0;i<ll.size();i++)
		{
			//if(x_min > ll.get(i).x){x_min = ll.get(i).x; idx_x_min = i;}
			//if(y_min > ll.get(i).y){y_min = ll.get(i).y; idx_y_min = i;}
			if(x_max < ll.get(i).x){x_max = ll.get(i).x; idx_x_max = i;}
			if(y_max < ll.get(i).y){y_max = ll.get(i).y; idx_y_max = i;}
		}
		long y_max_x_max = ll.get(idx_x_max).y;
		long x_max_y_max = ll.get(idx_y_max).x;
		for (int i=0;i<ll.size();i++)
		{
			if(Math.sqrt(Math.pow(y_max_x_max-ll.get(i).y, 2) + Math.pow(x_max - ll.get(i).x,2))<2 || Math.sqrt(Math.pow(y_max-ll.get(i).y, 2) + Math.pow(x_max_y_max - ll.get(i).x,2))<2)
				n++;
		}
		return n;
	}
	
	
}

	public static void main(String[] args)
    {  
		ArrayList<gesture> listGesture = new ArrayList<gesture>();
		ArrayList<gesture> list = new ArrayList<gesture>();
		String filepath = "D:/SEM-1/sketch/project/train.json";
        listGesture = readJsonFile(filepath);
        writeCSV(listGesture);
    }
	
	public static ArrayList<gesture> readJsonFile(String filePath) 
	{
		ArrayList<gesture> gg = new ArrayList<gesture>();
		BufferedReader br = null;
		JSONParser parser = new JSONParser();
		int q=0;
		try{
			br = new BufferedReader(new FileReader(filePath));
			String sCurrentLine ;
			//System.out.println(sCurrentLine);
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(q>=0)
				{
					//System.out.println("read"+q);
				}
				q++;
				project.gesture g1 = new project.gesture();
				Object obj = parser.parse(sCurrentLine);
				JSONObject jso = (JSONObject) obj;
				g1.id = (String) jso.get("id");
				g1.time = Long.valueOf((String) jso.get("time"));
				JSONArray arr = (JSONArray) jso.get("strokes");
				for(Object o:arr)
				{
					JSONObject op = (JSONObject) o;
					gesture.stroke p1 = new gesture.stroke();
					JSONArray arr1 = (JSONArray) op.get("points");
					for(Object oo:arr1)
					{
						if(oo!=null)
						{
							gesture.stroke_point s1 = new gesture.stroke_point();
							s1.id = oo.toString();
						//.id = (String) o1.get("id");
							p1.stroke_points.add(s1);
						}
					}
					g1.ls.add(p1);
				}
				
				JSONArray arr2 = (JSONArray) jso.get("points");
				for(Object o:arr2)
				{
					//	System.out.println(o);
					JSONObject oo = (JSONObject) o;
    		//	System.out.println(o);
    		//	System.out.println(oo);
    		//	gesture.point p1 = new gesture.point();
					gesture.point p1 = new gesture.point();
					p1.x = (long) oo.get("x");
					p1.y = (long) oo.get("y");
					p1.id = (String) oo.get("id");
					p1.time = Long.valueOf((String) oo.get("time"));
					g1.actual_points.add(p1);				
				}
				JSONArray js  = (JSONArray) jso.get("shapes");
				for(Object m:js)
				{
					JSONObject  oc = (JSONObject) m;
					g1.interpretation = (String) oc.get("interpretation");
					//g1.confidence = (int) oc.get("confidence");
				}
				
				
				

				gg.add(g1);
			}
			br.close();
	} catch (IOException | ParseException e) {
        e.printStackTrace();
    } finally {
        try {
            if (br != null)br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
		//System.out.println(features.sine_starting(gg.get(0).actual_points));
	//	System.out.println(features.cosine_starting(gg.get(0).actual_points));
		
		//for(int i=0;i<gg.size();i++)
		//{
		//	System.out.println("@@"+features.aspectAngle(gg.get(i).actual_points)+"@@");
			
	//	}
		
			//for(int j=0;j<gg.get(0).actual_points.size();j++)
				//System.out.println(gg.get(0).actual_points.get(j).x);
			
		/*
		for(int i=0;i< gg.get(0).actual_points.size();i++)
		{
			System.out.println(gg.get(0).actual_points.get(i).x);
		}
		gg.get(0).sampled_points = gesture.sampling(gg.get(0).actual_points);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@"+ gg.get(0).sampled_points.size());
		for(int i=0;i< gg.get(0).sampled_points.size();i++)
		{
			System.out.println(gg.get(0).sampled_points.get(i).x);
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@"+gg.get(0).actual_points.size());

		for(int i=0;i< gg.get(0).actual_points.size();i++)
		{
			System.out.println(gg.get(0).actual_points.get(i).x);
		}

		System.out.println(features.totalGestureLength(gg.get(0).actual_points));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(features.totalGestureLength(gg.get(0).sampled_points));
*/
		return gg;
		

}
	
	public static void writeCSV(ArrayList<gesture> list){
		String allfeaturesfile = "D:/SEM-1/sketch/project/allFeatures.csv";
		String optimalfeaturesfile = "D:/SEM-1/sketch/project/optimal.csv"; 
		
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(allfeaturesfile).exists();
			
		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter(allfeaturesfile, true), ',');
			if (!alreadyExists)
			{
				csvOutput.write("col1");
				csvOutput.write("col2");
				csvOutput.write("col3");
				csvOutput.write("col4");
				csvOutput.write("col5");
				csvOutput.write("col6");
				csvOutput.write("col7");
				csvOutput.write("col8");
				csvOutput.write("col9");
				csvOutput.write("col10");
				csvOutput.write("col11");
				csvOutput.write("col12");
				csvOutput.write("col13");
				csvOutput.write("col14");
				csvOutput.write("col15");
				csvOutput.write("col16");
				csvOutput.write("col17");
				csvOutput.write("col18");
				csvOutput.write("col19");
				csvOutput.write("col20");
				csvOutput.write("col21");
				csvOutput.write("col22");
				csvOutput.write("col23");
				csvOutput.write("col24");
				csvOutput.write("col25");
				csvOutput.write("col26");
				csvOutput.write("col27");
				csvOutput.write("col28");
				csvOutput.write("col29");
				csvOutput.write("col30");
				csvOutput.write("col31");
				csvOutput.endRecord();
			}
			for(int i=0;i<list.size();i++)
			{
				System.out.println(i);
				csvOutput.write(Double.toString(features.areaBoundingBox(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.aspectAngle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.averageCurvature(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.boudingBox_angle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.boudingBox_diagonal(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.cosine_starting(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.cosineendPoint(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.curviness(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.densityMatrixOne(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.densityMatrixTwo(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.endPointDistance(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.logAreaBoundingBox(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.logAspectAngle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.logTotalLength(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.maxStrokeToTotalStrokesLengthRatio(list.get(i))));
				csvOutput.write(Double.toString(features.openness(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.overlap(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.shaftLengthToTotalLengthRatio(list.get(i))));
				csvOutput.write(Double.toString(features.sine_starting(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.sineendPoint(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.thinness(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalAbsoluteAngle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalAngleOverTotalAbsoluteAngle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalAngleTraversed(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalAngleTraversedOverTotalLength(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalGestureLength(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalSquaredAngle(list.get(i).actual_points)));
				csvOutput.write(Double.toString(features.totalStrokesLength(list.get(i))));
				csvOutput.write(Integer.toString(features.densityNearEndPoints(list.get(i).actual_points)));
				csvOutput.write(Integer.toString(features.directionChanges(list.get(i).actual_points)));
				csvOutput.write(list.get(i).interpretation);
				csvOutput.endRecord();
			}
			csvOutput.close();

			
			CsvWriter csvOutput1 = new CsvWriter(new FileWriter(optimalfeaturesfile, true), ',');
			if (!alreadyExists)
			{
				csvOutput1.write("col1");
				csvOutput1.write("col2");
				csvOutput1.write("col3");
				csvOutput1.write("col4");
				csvOutput1.write("col5");
				csvOutput1.write("col6");
				csvOutput1.write("col7");
				csvOutput1.write("col8");
				csvOutput1.write("col9");
				csvOutput1.write("col10");
				csvOutput1.write("col11");
				csvOutput1.write("col12");
				csvOutput1.write("col13");
				csvOutput1.write("col14");
				csvOutput1.write("col15");
				csvOutput1.write("col16");
				csvOutput1.write("col17");
				csvOutput1.write("col18");
				csvOutput1.write("col19");
				csvOutput1.write("col20");
				/*csvOutput.write("col21");
				csvOutput.write("col22");
				csvOutput.write("col23");
				csvOutput.write("col24");
				csvOutput.write("col25");
				csvOutput.write("col26");
				csvOutput.write("col27");
				csvOutput.write("col28");
				csvOutput.write("col29");
				csvOutput.write("col30");
				csvOutput.write("col31");*/
				csvOutput1.endRecord();
			}
			for(int i=0;i<list.size();i++)
			{
				System.out.println(i);
				csvOutput1.write(Double.toString(features.areaBoundingBox(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.aspectAngle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.averageCurvature(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.boudingBox_angle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.boudingBox_diagonal(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.cosine_starting(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.cosineendPoint(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.curviness(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.densityMatrixOne(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.densityMatrixTwo(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.endPointDistance(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.logAreaBoundingBox(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.logAspectAngle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.logTotalLength(list.get(i).actual_points)));
				//csvOutp1ut.write(Double.toString(features.maxStrokeToTotalStrokesLengthRatio(list.get(i))));
				//csvOutput.write(Double.toString(features.openness(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.overlap(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.shaftLengthToTotalLengthRatio(list.get(i))));
				//csvOutput.write(Double.toString(features.sine_starting(list.get(i).actual_points)));
				//csvOutput.write(Double.toString(features.sineendPoint(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.thinness(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalAbsoluteAngle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalAngleOverTotalAbsoluteAngle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalAngleTraversed(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalAngleTraversedOverTotalLength(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalGestureLength(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalSquaredAngle(list.get(i).actual_points)));
				csvOutput1.write(Double.toString(features.totalStrokesLength(list.get(i))));
				//csvOutput.write(Integer.toString(features.densityNearEndPoints(list.get(i).actual_points)));
				//csvOutput.write(Integer.toString(features.directionChanges(list.get(i).actual_points)));
				csvOutput1.write(list.get(i).interpretation);
				csvOutput1.endRecord();
			}
			
			
			csvOutput1.close();

			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static ArrayList<gesture> preprocessing(ArrayList<gesture> ll){
		
		ArrayList<gesture> result = new ArrayList<gesture>();
		for(int i=0;i<ll.size();i++)
		{
			for(int j=0;j<ll.get(i).actual_points.size()-1;j++)
			{
				
				if((ll.get(i).actual_points.get(j).x == ll.get(i).actual_points.get(j+1).x && ll.get(i).actual_points.get(j).y == ll.get(i).actual_points.get(j+1).y ))
				{
					ll.get(i).actual_points.remove(j+1);
				}
				
			}
			
		}
		
		
		return ll;
	}
}
