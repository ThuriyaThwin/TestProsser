/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.collections15.Transformer;




import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;

/**
 * Demonstrates 3 views of one graph in one model with one layout.
 * Each view uses a different scaling graph mouse.
 * 
 * @author Tom Nelson 
 * 
 */
@SuppressWarnings("serial")
public class GraphPanel<V,E> extends JPanel {

    /**
     * the graph
     */
    Graph<V,E> graph;

    /**
     * the visual components and renderers for the graph
     */
    VisualizationViewer<V,E> vv1;
    
    /**
     * the normal transformer
     */
//    MutableTransformer transformer;
    
    Dimension preferredSize = new Dimension(600,800);
    
    final String messageOne = "The mouse wheel will scale the model's layout when activated"+
    " in View 1. Since all three views share the same layout transformer, all three views will"+
    " show the same scaling of the layout.";
    
    final String messageTwo = "The mouse wheel will scale the view when activated in"+
    " View 2. Since all three views share the same view transformer, all three views will be affected.";
    
    final String messageThree = "   The mouse wheel uses a 'crossover' feature in View 3."+
    " When the combined layout and view scale is greater than '1', the model's layout will be scaled."+
    " Since all three views share the same layout transformer, all three views will show the same "+
    " scaling of the layout.\n   When the combined scale is less than '1', the scaling function"+
    " crosses over to the view, and then, since all three views share the same view transformer,"+
    " all three views will show the same scaling.";
    
    JTextArea textArea;
    JScrollPane scrollPane;
    
    /**
     * create an instance of a simple graph in two views with controls to
     * demo the zoom features.
     * 
     */
    @SuppressWarnings("unchecked")
	public GraphPanel(	Graph<V,E> graph  ) {
        
    	super(new GridLayout(1,0));
    	 this.graph = graph;
        	//TestGraphs.getOneComponentGraph();
        
        // create one layout for the graph
		//ISOMLayout<AbstractAgent,Number> layout = new ISOMLayout<AbstractAgent,Number>(graph);
    	 CircleLayout<V,E> layout = new CircleLayout<V,E>(graph);
        // create one model that all 3 views will share
        DefaultVisualizationModel<V,E> visualizationModel =
            new DefaultVisualizationModel<V,E>(layout, preferredSize);
 
        // create 3 views that share the same model
        vv1 = new VisualizationViewer<V,E>(visualizationModel, preferredSize);
       
        vv1.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
       
        // this class will provide both label drawing and vertex shapes
        VertexLabelAsShapeRenderer<V,E> vlasr = new VertexLabelAsShapeRenderer<V,E>(vv1.getRenderContext());
        
     // customize the render context
        
        vv1.getRenderContext().setVertexLabelTransformer(
        		// this chains together Transformers so that the html tags
        		// are prepended to the toString method output
        		new Transformer<V,String>() {
					public String transform(V input) {
						return input.toString();
					}
				}
				);
       
        vv1.getRenderContext().setVertexShapeTransformer(vlasr);
        vv1.getRenderer().setVertexLabelRenderer(vlasr);
        vv1.getRenderer().setVertexRenderer(new GradientVertexRenderer<V,E>(Color.gray, Color.white, true));
        vv1.setBackground(Color.white);
        final JPanel p1 = new JPanel(new BorderLayout());
        
        p1.add(new GraphZoomScrollPane(vv1));
       /*
        JButton h1 = new JButton("?");
        h1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText(messageOne);
                JOptionPane.showMessageDialog(p1, scrollPane, 
                        "View 1", JOptionPane.PLAIN_MESSAGE);
            }});
*/
        
        // create a GraphMouse for each view
        // each one has a different scaling plugin
        /*
        DefaultModalGraphMouse gm1 = new DefaultModalGraphMouse() {
            protected void loadPlugins() {
                pickingPlugin = new PickingGraphMousePlugin();
                animatedPickingPlugin = new AnimatedPickingGraphMousePlugin();
                translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
                scalingPlugin = new ScalingGraphMousePlugin(new LayoutScalingControl(), 0);
                rotatingPlugin = new RotatingGraphMousePlugin();
                shearingPlugin = new ShearingGraphMousePlugin();

                add(scalingPlugin);
                setMode(Mode.TRANSFORMING);
            }
        };

        
        vv1.setGraphMouse(gm1);


        vv1.setToolTipText("<html><center>MouseWheel Scales Layout</center></html>");
 
*/                

        this.add(p1);
        //content.add(panel);
        

    }
    
    
    /**
     * A demo class that will make vertices larger if they represent
     * a collapsed collection of original vertices
     * @author Tom Nelson
     *
     * @param <V>
     */
    class ClusterVertexSizeFunction<X> implements Transformer<X,Integer> {
    	int size;
        public ClusterVertexSizeFunction(Integer size) {
            this.size = size;
        }

        public Integer transform(X v) {
            if(v instanceof Graph) {
                return 30;
            }
            return size;
        }
    }
    
    class RectangleVertexShapeFunction<VV> extends EllipseVertexShapeTransformer<VV> {

    	RectangleVertexShapeFunction() {
            setSizeTransformer(new ClusterVertexSizeFunction<VV>(20));
        }
    	
		@Override
        public Shape transform(VV v) {
            if(v instanceof Graph) {
                return factory.getRectangle(v);
            }
            return super.transform(v);
        }
    }
     

}
