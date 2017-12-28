package com.liyuncong.learn.nlptask.stanfordcorenlp;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class GrammarTreeFrame extends JFrame {
	private static final long serialVersionUID = -3763028538806157006L;
	
	public GrammarTreeFrame() {
		setSize(1800, 800);
	}
	
	public static void showTree(final GrammarNode root) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				GrammarTreeFrame treeFrame = new GrammarTreeFrame();
				treeFrame.setTitle("语法树");
				treeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				treeFrame.add(new GrammarNodeComponent(root));
				treeFrame.setVisible(true);
			}
		});
	}
	
	public static class GrammarNode {
		private String text;
		private List<GrammarNode> children = new LinkedList<>();
		public void addChild(GrammarNode child) {
			assert child != null;
			children.add(child);
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public List<GrammarNode> getChildren() {
			return children;
		}
		public void setChildren(List<GrammarNode> children) {
			this.children = children;
		}
		/**
		 * 
		 * @param StanfordTreeString 
		 * @return
		 */
		public static GrammarNode parseStanfordTreeString(String stanfordTreeString) {
			int end = stanfordTreeString.indexOf('(', 1);
			GrammarNode grammarNode = new GrammarNode();
			if (end == -1) {
				grammarNode.setText(stanfordTreeString.substring(1, stanfordTreeString.length() - 1));
			} else {
				grammarNode.setText(stanfordTreeString.substring(1, end).trim());
				String childrenText = stanfordTreeString.substring(end, stanfordTreeString.length() -1);
				LinkedList<Integer> stack = new LinkedList<>();
				for(int i = 0; i < childrenText.length(); i++) {
					if (childrenText.charAt(i) == '(') {
						stack.push(i);
					} else if (childrenText.charAt(i) == ')') {
						Integer start = stack.pop();
						if (stack.isEmpty()) {
							grammarNode.addChild(parseStanfordTreeString(childrenText.substring(start, i + 1)));
						}
					}
				}
			}
			return grammarNode;
		}
		@Override
		public String toString() {
			return "GrammarNode [text=" + text + ", children=" + children + "]";
		}
	}
	
	private static class GrammarNodeWithPosition {
		private GrammarNode grammarNode;
		private int x;
		private int y;
		public GrammarNode getGrammarNode() {
			return grammarNode;
		}
		public void setGrammarNode(GrammarNode grammarNode) {
			this.grammarNode = grammarNode;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		@Override
		public String toString() {
			return "GrammarNodeWithPosition [grammarNode=" + grammarNode + ", x=" + x + ", y=" + y + "]";
		}
		
	}
	
	public static class GrammarNodeComponent extends JComponent {
		private GrammarNode root;
		private Map<Integer, List<GrammarNodeWithPosition>> levelAndGrammarNode = new HashMap<>();
		
		public GrammarNodeComponent(GrammarNode root) {
			super();
			this.root = root;
			levelAndGrammarNode = hierarchicalTraversal(root);
		}

		private static Map<Integer, List<GrammarNodeWithPosition>> hierarchicalTraversal(GrammarNode root) {
			Map<Integer, List<GrammarNodeWithPosition>> levelAndGrammarNode = new HashMap<>();
			GrammarNodeWithPosition sentinel = new GrammarNodeWithPosition();
			LinkedList<GrammarNodeWithPosition> queue = new LinkedList<>();
			GrammarNodeWithPosition rootWithPosition = new GrammarNodeWithPosition();
			rootWithPosition.setGrammarNode(root);
			queue.addLast(rootWithPosition);
			queue.addLast(sentinel);
			int level = 1;
			List<GrammarNodeWithPosition> thisLevelNode = new LinkedList<>();
			while (!queue.isEmpty()) {
				GrammarNodeWithPosition grammarNodeWithPosition = queue.removeFirst();
				thisLevelNode.add(grammarNodeWithPosition);
				if (grammarNodeWithPosition == sentinel) {
					levelAndGrammarNode.put(level, thisLevelNode);
					thisLevelNode = new LinkedList<>();
					if (!queue.isEmpty()) {
						queue.addLast(sentinel);
						level++;
					}
				} else {
					for(GrammarNode child : grammarNodeWithPosition.getGrammarNode().getChildren()) {
						GrammarNodeWithPosition childWithPosition = new GrammarNodeWithPosition();
						childWithPosition.setGrammarNode(child);
						queue.addLast(childWithPosition);
					}
				}
			}
			return levelAndGrammarNode;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics2d = (Graphics2D) g;
			drawGrammarNode(graphics2d, root, 1000, 20, 1);
		}
		
		private void drawGrammarNode(Graphics2D graphics2d, GrammarNode grammarNode, int x, int y, int level) {
			graphics2d.drawString(grammarNode.getText(), x, y);
			List<GrammarNode> children = grammarNode.getChildren();
			int childrenNum = children.size();
			if (childrenNum == 0) {
				return;
			}
			int half = children.size() / 2;
			int newY = y + 50;
			for(int i = 0; i < childrenNum; i++) {
				GrammarNode child = children.get(i);
				int newX = generateX(level + 1, x, child, i, half);
				graphics2d.draw(new Line2D.Double(x, y + 12, newX, newY - 12));
				drawGrammarNode(graphics2d, child, newX, newY, level + 1);
			}
		}
		
		private int generateX(int level, int parentX, GrammarNode grammarNode, int childSerialNum, int half) {
			int x = parentX + (childSerialNum - half) * 40 * ((int) (level * 0.7));
			List<GrammarNodeWithPosition> thisLevelGrammarNodes = levelAndGrammarNode.get(level);
			GrammarNodeWithPosition previous = null; 
			GrammarNodeWithPosition now = null;
			for (GrammarNodeWithPosition grammarNodeWithPosition : thisLevelGrammarNodes) {
				previous = now;
				now = grammarNodeWithPosition;
				if (now.getGrammarNode() == grammarNode) {
					break;
				}
			}
			if (previous != null) {
				int previouX = previous.getX();
				int previousTextLen = previous.getGrammarNode().getText().length();
				if (x < previouX + previousTextLen * Constants.CHAR_WIDTH) {
					x = previouX + previousTextLen * Constants.CHAR_WIDTH + 10;
				}
			}
			now.setX(x);
			return x;
		}
		
	}
	
	private static class Constants {
		public static final int CHAR_WIDTH = 10;
		public static final int CHAR_HEIGHT = 10;
	}
	
	public static void main(String[] args) {
		String stanfordParseString = "(ROOT (IP (ADVP (AD 随手)) (NP (NN 科技)) (VP (VC 是) (NP (NP (NR 金蝶)) (NP (NN 旗下) (NN 子公司))))))";
		GrammarNode grammarNode = GrammarNode.parseStanfordTreeString(stanfordParseString);
		GrammarTreeFrame.showTree(grammarNode);
	}
}
