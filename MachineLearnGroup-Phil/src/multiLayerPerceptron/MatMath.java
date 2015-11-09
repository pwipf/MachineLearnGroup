

package multiLayerPerceptron;

// helper class MatMath
class MatMath {
	static public double[][] matMult(double[][] a, double[][] b){
		int m=a.length;
		int n=a[0].length;
		int p=b[0].length;
		double sum;
		double[][] c=new double[m][p];
		for(int i=0;i<m;i++)
			for(int j=0;j<p;j++){
				sum=0;
				for(int k=0;k<n;k++)
					sum+=a[i][k]*b[k][j];
				c[i][j]=sum;
			}
		return c;
	}

	static public double[] matMult(double[][] a, double[] b){
		int m=a.length;
		int n=a[0].length;
		double sum;
		double[] c=new double[m];
		for(int i=0;i<m;i++){
				sum=0;
				for(int k=0;k<n;k++)
					sum+=a[i][k]*b[k];
				c[i]=sum;
			}
		return c;
	}

	static public double[][] matMult(double[] a, double[] b){
		int m=a.length;
		int n=b.length;
		double[][]c = new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i]*b[j];
		return c;
	}

	static public double[] vectorize(double s){
		double[] m=new double[1];
		m[0]=s;
		return m;
	}

	static public double[] vecAdd(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]+w[i];
		return u;
	}

	static public double[] vecSub(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]-w[i];
		return u;
	}

	static public double[] vElemMult(double[] v, double[] w){
		double[] u=new double[v.length];
		for(int i=0;i<v.length;i++)
			u[i]=v[i]*w[i];
		return u;
	}

	static public double[][] transpose(double[][]a){
		int m=a.length;
		int n=a[0].length;
		double[][]t=new double[n][m];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				t[j][i]=a[i][j];
		return t;
	}

	static public double[][] matAdd(double[][]a,double[][]b){
		int m=a.length;
		int n=a[0].length;
		double[][]c=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i][j]+b[i][j];
		return c;
	}

	static public double[][] matSub(double[][]a,double[][]b){
		int m=a.length;
		int n=a[0].length;
		double[][]c=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				c[i][j]=a[i][j]-b[i][j];
		return c;
	}

	static public double[] vecMult(double s,double[] v){
		double[] w=new double[v.length];
		for(int i=0;i<v.length;i++)
			w[i]=v[i]*s;
		return w;
	}

	static public double[][] matMult(double s,double[][] a){
		int m=a.length;
		int n=a[0].length;
		double[][] b=new double[m][n];
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				b[i][j]=a[i][j]*s;
		return b;
	}

	// helpful map function (from arduino library)
	static double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
