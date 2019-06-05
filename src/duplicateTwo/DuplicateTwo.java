package duplicateTwo;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import static duplicateTwo.NumberField.*;

enum Size {
	BIG(COL*70, ROW*70, new Font("consolas", Font.BOLD, 25)),
	SMALL(COL*40, ROW*40, new Font("consolas", Font.BOLD, 15));
	
	private int width;
	private int height;
	private Font font;
	
	private Size(int width, int height, Font font) {
		this.width = width;
		this.height = height;
		this.font = font;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Font getFont() {
		return font;
	}
}

public class DuplicateTwo extends JFrame implements ActionListener, KeyListener {
	
	private static final ArrayList<Color> COLOR_ARR = new ArrayList<>();
	static {
		COLOR_ARR.add(new Color(255, 220, 70)); // 1
		COLOR_ARR.add(new Color(220, 255, 80)); // 2
		COLOR_ARR.add(new Color(80, 220, 255)); // 4
		COLOR_ARR.add(new Color(240, 90, 90)); // 8
		COLOR_ARR.add(new Color(90, 240, 90)); // 16
		COLOR_ARR.add(new Color(120, 120, 240)); // 32
		COLOR_ARR.add(new Color(180, 210, 110)); // 64
		COLOR_ARR.add(new Color(255, 180, 110)); // 128
		COLOR_ARR.add(new Color(255, 128, 128)); // 256
		COLOR_ARR.add(new Color(255, 0, 0)); // 512
		COLOR_ARR.add(new Color(120, 240, 120)); // 1024
		COLOR_ARR.add(new Color(255, 0, 255)); // 2048
		COLOR_ARR.add(new Color(25, 100, 150)); // 4096
		COLOR_ARR.add(new Color(150, 100, 25)); // 8192
		COLOR_ARR.add(new Color(200, 50, 50)); // 16384
		COLOR_ARR.add(new Color(255, 110, 180)); // 32768
		
	}

	private JMenuBar jmb = new JMenuBar();
	private JMenu mSet = new JMenu("메뉴");
	private JMenuItem iReset = new JMenuItem("Reset");
	private JMenuItem iSize = new JMenuItem("BIG");
	private JMenuItem iBack = new JMenuItem("Back");
	
	private JPanel pPane = new JPanel(new GridLayout(ROW, COL));
	private JButton[][] bPane = new JButton[ROW][COL];
	
	private NumberField numField = new NumberField();
	
	private boolean isSizeBig = false;
	private boolean isGameOver = false;
	
	private long time;
	DuplicateTwo(String title) {
		super(title);
		
		EtchedBorder etchedBorder = new EtchedBorder();
		
		for(int i=0; i<bPane.length; i++) {
			for(int j=0; j<bPane[i].length; j++) {
				bPane[i][j] = new JButton();
				bPane[i][j].setBorder(etchedBorder);
				bPane[i][j].addKeyListener(this);
				pPane.add(bPane[i][j]);
			}
		}
		
		add(pPane);
		
		iReset.addActionListener(this);
		iSize.addActionListener(this);
		iBack.addActionListener(this);
		
		mSet.add(iReset);
		mSet.add(iSize);
		mSet.add(iBack);
		jmb.add(mSet);
		setJMenuBar(jmb);
		
		setVisible(true);
		setLocation(100, 100);
		setSize(Size.SMALL);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		initialize();
	}
	
	public static void main(String[] args) {
		new DuplicateTwo("2048");
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(iReset)) {
			initialize();
		} else if(ae.getSource().equals(iSize)) {
			turnSize();
		} else if(ae.getSource().equals(iBack)) {
			backToPrevious();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent ke) {
		NumberField tmpField = new NumberField(numField);
		numField.duplicateTwo(Direction.getDirectionByCode(ke.getKeyCode()));
		
		if(numField.hasChange(tmpField)) {
			numField.setPrevious(tmpField);
			numField.randomGenerate();
			update();
		}
		
		if(!isGameOver && numField.has2048()) {
			time = System.currentTimeMillis()/1000 - time;
			int ans = JOptionPane.showConfirmDialog(this, "성공 ! 계속 하시겠습니까 ?\n걸린시간 : "+time+"초");
			if(ans == JOptionPane.YES_OPTION) {
				isGameOver = true;
			} else {
				gameOver();
			}
		}
		
		if(!numField.isMoveable()) {
			int ans = JOptionPane.showConfirmDialog(this, "실패 ! 한 턴 전으로 돌아가시겠습니까 ?");
			if(ans == JOptionPane.YES_OPTION) {
				backToPrevious();
			} else {
				gameOver();
			}
		}
	}
	
	public void initialize() {
		time = System.currentTimeMillis()/1000;
		isGameOver = false;
		numField = new NumberField();
		for(int i=0; i<bPane.length; i++)
			for(int j=0; j<bPane[i].length; j++)
				if(bPane[i][j].getKeyListeners().length == 0)
					bPane[i][j].addKeyListener(this);
		update();
	}
	
	public void backToPrevious() {
		if(numField.getPrevious() == null)
			return;
		numField = numField.getPrevious();
		update();
	}
	
	
	public void update() {
		for(int i=0; i<bPane.length; i++) {
			for(int j=0; j<bPane[i].length; j++) {
				if(numField.getNum(i, j) != EMPTY) {
					bPane[i][j].setText(numField.getNum(i, j)+"");
				} else {
					bPane[i][j].setText("");
				}
				bPane[i][j].setBackground(COLOR_ARR.get(log2(numField.getNum(i, j))));
			}
		}
	}
	
	public void turnSize() {
		if(isSizeBig) {
			setSize(Size.SMALL);
			iSize.setText("BIG");
		} else {
			setSize(Size.BIG);
			iSize.setText("SMALL");
		}
		isSizeBig = !isSizeBig;
	}
	
	public void setSize(Size size) {
		setSize(size.getWidth()+getInsets().left+getInsets().right ,
				size.getHeight()+getInsets().top+getInsets().bottom+
				jmb.getHeight());
		
		for(int i=0; i<bPane.length; i++)
			for(int j=0; j<bPane[i].length; j++)
				bPane[i][j].setFont(size.getFont());
	}
	
	public void gameOver() {
		for(int i=0; i<bPane.length; i++)
			for(int j=0; j<bPane[i].length; j++)
				bPane[i][j].removeKeyListener(this);
		isGameOver = true;
	}
	
	private static int log2(int x) {
		int res = 0;
		while(x>=2) {
			x/=2;
			res++;
		}
		return res;
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}
