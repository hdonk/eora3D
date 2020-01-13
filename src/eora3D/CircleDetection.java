package eora3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

class CircleDetection {
    private BufferedImage grey;
    private int[][] sobelX;
    private int[][] sobelY;
    private double[][] sobelTotal;
    public int threshold = 280;
	public int[][][] A;
	
	int minr = 10;
	int maxr = 40;
	
	BufferedImage displayimage = null;
	public int minHits = 200;
	
	Rectangle searcharea = null;
	private BufferedImage totalCircles = null;

    void detect(BufferedImage image, Rectangle a_searcharea) throws Exception{

    	searcharea = a_searcharea;
    	
        //runs the required processes for circle detection, ie sobel edge detection
        toGrayScale(image);
        edgeDetection();

        //creates and outputs images for the sobel sweep in x and y direction, and the combined sobel output
/*        BufferedImage img = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                img.setRGB(i, j, sobelX[i][j]);
            }
        }
        displayimage = img;*/


/*        BufferedImage ing = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                ing.setRGB(i, j, sobelY[i][j]);
            }
        }*/

        BufferedImage total = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        double max = 0;
        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                if(sobelTotal[i][j]>max){
                    max = sobelTotal[i][j];
                }
            }
        }
        //displayimage = total;
      for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                //maps every pixel to a grayscale value between 0 and 255 from between 0 and the max value in sobelTotal
                int rgb = new Color((int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255), 255).getRGB();
                total.setRGB(i,j,rgb);
            }
        }
        total = changeBrightness(20.0f, total);
        //displayimage = total;
        
        //outputs an image showing every pixel that is above the threshold
        BufferedImage totalColour = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < grey.getWidth(); i++) {
        	for(int j = 0; j < grey.getHeight(); j++) {
        		Color c1 = new Color(0,255,0);
        		if(sobelTotal[i][j] > threshold) {
        			totalColour.setRGB(i, j, c1.getRGB());
        		}
        	}
        }
        //displayimage = totalColour;
        
        //circleDetection(total);
    }

    //maps the given value between startCoord1 and endCoord1 to a value between startCoord2 and endCoord2
    private double map(double valueCoord1,
                             double startCoord1, double endCoord1,
                             double startCoord2, double endCoord2) {


        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + startCoord2;
    }

    //converts given image into a grayscale image
    private void toGrayScale(BufferedImage img) throws Exception{
        grey = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_BYTE_GRAY);
        grey.getGraphics().drawImage(img, 0 , 0, null);
    }

    //runs all the functions required to complete edge detection
    private void edgeDetection(){
        calcSobelX();
        calcSobelY();
        combineSobel();
    }

    //performs the horizontal sobel sweep, sets up the matrix using a 3x3 array and runs through
    //every pixel to calculate the sobel result
    private void calcSobelX(){
        sobelX = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[1][0] = -2;
        base[2][0] = -1;
        base[0][1] = 0;
        base[1][1] = 0;
        base[2][1] = 0;
        base[0][2] = 1;
        base[1][2] = 2;
        base[2][2] = 1;


        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelX[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    //performs the vertical sobel sweep, sets up the matrix using a 3x3 array and runs through
    //every pixel to calculate the sobel result
    private void calcSobelY(){
        sobelY = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[0][1] = -2;
        base[0][2] = -1;
        base[1][0] = 0;
        base[1][1] = 0;
        base[1][2] = 0;
        base[2][0] = 1;
        base[2][1] = 2;
        base[2][2] = 1;


        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelY[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    //a series of if statements to account for any edge cases to ensure no errors, calculates
    //the sobel result for any pixel and kernel
    private int getSobelResult(int x, int y, int[][] base){
        int result = 0;
        if(x==0 && y ==0){
            for(int i = 0; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y==grey.getHeight()-1){
            for(int i = 0; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y !=0){
            for (int i = 0; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result += new Color(grey.getRGB(x + i, y + j)).getRed()* base[j + 1][i + 1];
                }
            }

        }else if(x==grey.getWidth()-1 && y ==0){
            for(int i = -1; i <= 0; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=0 && y ==0){
            for(int i = -1; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y!=grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else{
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }

        return result;
    }

    //performs the algorithm to combine the horizontal sweep and vertical sweep
    private void combineSobel(){
        sobelTotal = new double[grey.getWidth()][grey.getHeight()];
        for(int i = searcharea.x; i< searcharea.x+searcharea.width; i++){
            for(int j = searcharea.y; j<searcharea.y+searcharea.height; j++){
                sobelTotal[i][j] = Math.round(Math.sqrt(Math.pow((double)sobelX[i][j],2) + Math.pow((double)sobelY[i][j],2)));
            }
        }
    }

    void initTotalCircles(BufferedImage image)
    {
    	totalCircles = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	
    }
    
    Rectangle findFirstCircle(BufferedImage image) throws Exception 
    {
        int radius = maxr;
        A = new int[image.getWidth()][image.getHeight()][radius];

        for (int rad = 0; rad < radius; rad++) {
            for (int x = searcharea.x; x < searcharea.x+searcharea.width; x++) {
                for (int y = searcharea.y; y < searcharea.y+searcharea.height; y++) {
                    //if the given pixel is above the threshold, a circle will be drawn at radius rad around it and if it
                    //is a valid coordinate it will be accumulated in the A array and plotted in the pointSpace image
                    if (sobelTotal[x][y] > threshold) {
                        for (int t = 0; t <= 360; t++) {
                            Integer a = (int) Math.floor(x - rad * Math.cos(t * Math.PI / 180));
                            Integer b = (int) Math.floor(y - rad * Math.sin(t * Math.PI / 180));
                            if (!((0 > a || a > image.getWidth() - 1) || (0 > b || b > image.getHeight() - 1))) {
                                if (!(a.equals(x) && b.equals(y))) {
                                   A[a][b][rad] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        Graphics2D g = null;
        if(totalCircles != null)
        {
        	displayimage = totalCircles;
        	g = totalCircles.createGraphics();
        	g.setColor(Color.RED);
        }

//        System.out.println("Looking for potential centers");
        for (int x = searcharea.x; x < searcharea.x+searcharea.width; x++) {
            for (int y = searcharea.y; y < searcharea.y+searcharea.height; y++) {
                for (int r = minr; r < maxr; r++) {
                    if (A[x][y][r] > minHits) {
                        double a =  x - r * Math.cos(0 * Math.PI / 180);
                        double b =  y - r * Math.sin(90 * Math.PI / 180);
                      if(g != null) g.drawOval((int)a,(int)b,2*r,2*r);
//                        System.out.println("A "+A[x][y][r]+ "("+x+","+y+") r="+r);
                        return new Rectangle(x, y, r, r);
                    }
                }
            }
        }
        return null;
    }

    //changes the brightness of an image by the factor given
    private static BufferedImage changeBrightness(float brightenFactor, BufferedImage image){
        RescaleOp op = new RescaleOp(brightenFactor, 0, null);
        image = op.filter(image, image);
        return image;
    }
}