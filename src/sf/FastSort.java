package sf;
import java.util.Arrays;
public class FastSort{
	static int[] px(int [] ints) {
		int size=ints.length;
		for(int i=1;i<size;i++) {
			int j=i-1;
			int key=ints[i];
			while(j>=0&&ints[j]>key) {
				ints[j+1]=ints[j];
				j--;
			}
			ints[j+1]=key;			
		}
		return ints;
	}
	public static void main(String[] args) {
		System.out.println(Arrays.toString(px(Generate.intArray(150))));
	}
}