package pipeep.pathfinding;

import pipeep.geometry.Node;
import pipeep.geometry.Line;
import pipeep.geometry.Polygon;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * Representing a game board, this class is composed of a set of
 * <code>Polygon</code>s which are non-moveable. These can represent PVC,
 * blocks, or any other place you wish for the robot to avoid. Knowing
 * everything about the game's design, this class can do such things as tell if
 * a Node is within the line-of-sight or another Node, or even utilize
 * Dijkstra's algorithm to find the shortest possible path from one Node to
 * another. Note that this board is not entirely realistic. It assumes each Node
 * is simply an infinitely tiny point in space, and that robots are Nodes. For
 * realistic purposes, utiltize the <code>getExpanded()</code> function first.
 * <p/>
 * 
 * Largely acts like a wrapper around a HashSet of the polygons forming the
 * board. (using inheritance by composition rather than inheritance) Notably,
 * this class is mutable, but the polygons, nodes, and lines composing it are
 * not. Additionally, mutating this class causes <strong>all</strong> caches to
 * be marked as "dirty", requring them to be re-built on next use, potentially
 * causing a rather severe performance hit.<p/>
 * 
 * This class makes great use of caching and lazy evaluation to ensure the best
 * possible performance of the underlying features and algorithms used.
 */
public class Board implements Collection<Polygon> {
	// lazily evaluated
	private Set<Line> lines = null;
	private Set<Node> nodes = null;
	private Map<Node, Map<Node, Boolean>> navigationMesh = null;
	private Map<Node, Map<Node, Boolean>> unownedNavigationMesh = null;
	
	private Set<Polygon> polygons; // the underlying structure of this class
	
	public Board() {
		polygons = new HashSet<Polygon>();
	}
	
	public Board(Polygon ... p) {
		polygons = new HashSet<Polygon>(Arrays.asList(p));
	}
	
	
	// getters
	
	/**
	 * Returns the underlying polygon that this class acts as a wrapper around.
	 * <strong>Do not</strong> modify this returned object, as that could have
	 * unforseen consequences.
	 */
	public Set<Polygon> getPolygons() {
		return polygons;
	}
	
	/**
	 * Returns a set of all the lines composing all the polygons on this board.
	 * <strong>Do not</strong> modify this returned object, as that could have
	 * unforseen consequences.
	 */
	public Set<Line> getLines() {
		if(lines == null) {
			// the size must be at least 3 times the number of polgons (every
			// polygon must at least be a triangle)
			lines = new HashSet<Line>(polygons.size() * 3);
			for(Polygon p : getPolygons()) {
				lines.addAll(Arrays.asList(p.getLines()));
			}
		}
		return lines;
	}
	
	/**
	 * Returns a set of all the nodes composing all the polygons on this board.
	 * <strong>Do not</strong> modify this returned object, as that could have
	 * unforseen consequences.
	 */
	public Set<Node> getNodes() {
		if(nodes == null) {
			// the size must be at least 3 times the number of polgons (every
			// polygon must at least be a triangle)
			nodes = new HashSet<Node>(polygons.size() * 3);
			for(Polygon p : getPolygons()) {
				nodes.addAll(Arrays.asList(p.getNodes()));
			}
		}
		return nodes;
	}
	
	/**
	 * Returns an copy of this board, in which every polygon has been expanded
	 * by the given outset, in the way given by
	 * <code>Polygon.getExpanded(double outset)</code>.
	 * 
	 * @see  Polygon#getExpanded
	 */
	public Board getExpanded(double outset) {
		Board b = new Board();
		for(Polygon p: getPolygons()) {
			b.add(p.getExpanded(outset));
		}
		return b;
	}
	
	
	// Visibility stuff
	
	/**
	 * The <code>unownedNavigationMesh</code> map is for navigation information
	 * computed about <code>Node</code>s that are not on our board, but rather,
	 * that we have been called about. This Map may be replaced with one that
	 * uses something like <code>SoftReference</code>s for memory-aware caching.
	 * (a non-memory aware version could potentially lead to memory leaks in
	 * very large-scale operations)
	 */
	private Map<Node, Map<Node, Boolean>> getUnownedNavigationMesh() {
		if(unownedNavigationMesh == null) {
			// TODO: Replace with a smarter cache Map
			unownedNavigationMesh = new HashMap<Node, Map<Node, Boolean>>();
		}
		return unownedNavigationMesh;
	}
	
	/**
	 * A cache of pre-computed information on what nodes are visible from other
	 * nodes. This map only includes information amount nodes on our board,
	 * making it the "owned" navigation mesh.
	 */
	private Map<Node, Map<Node, Boolean>> getNavigationMesh() {
		if(navigationMesh == null) {
			navigationMesh = new HashMap<Node, Map<Node, Boolean>>(
				getNodes().size()
			);
			for(Node n: getNodes()) {
				navigationMesh.put(
					n, new HashMap<Node, Boolean>(getNodes().size() - 1)
				);
			}
		}
		return navigationMesh;
	}
	
	/**
	 * Gets the already known information about what node is directly visible
	 * from another node, <code>n</code>. It tries to get the mesh data in the
	 * following fashion:
	 * <ol>
	 *     <li>Looks at the "owned" navigation mesh</li>
	 *     <li>Looks at the "unowned" navigation mesh</li>
	 *     <li>Makes a new mesh, adding it to the "unowned" navigation mesh</li>
	 * </ol>
	 * <strong>Note:</strong> Because of certain optimizations, certain
	 * information may be known about the visibility of another node,
	 * <code>b</code>, but will not be expressed in node <code>n</code>'s mesh,
	 * therefore it may make sense to check both node <code>n</code>'s and
	 * <code>b</code>'s navigation meshes for visibility information.
	 * Additionally, navigation meshes are only caches, and as such, they can
	 * only include information about previously calculated things, and can
	 * potentially be "forgetful".
	 */
	private Map<Node, Boolean> getNavigationMesh(Node n) {
		Map<Node, Boolean> r = getNavigationMesh().get(n);
		if(r == null) {
			r = getUnownedNavigationMesh().get(n);
		} if(r == null) {
			r = getUnownedNavigationMesh().put(n, new HashMap<Node, Boolean>());
		}
		return r;
	}
	
	/**
	 * Returns a set of all nodes visible from the point of view in the given
	 * map.
	 */
	protected Set<Node> getVisibleIn(Node pov, Node ... map) {
		Set<Node> visible = new HashSet<Node>();
		for(Node n : map) {
			if(isVisible(pov, n)) {
				visible.add(n);
			}
		}
		return visible;
	}
	
	/**
	 * Returns a set of all nodes visible from the point of view on the board,
	 * as well as any additional ones specified as extra arguments. This is
	 * different from <code>getNavigationMesh</code>, as it actively computes
	 * missing visibility information, rather than only displaying the cache.
	 */
	protected Set<Node> getVisible(Node pov, Node ... extras) {
		Set<Node> visible = getVisibleIn(pov, extras);
		for(Node n : getNodes()) {
			if(isVisible(pov, n)) {
				visible.add(n);
			}
		}
		return visible;
	}
	
	/**
	 * Returns a boolean describing if the node passed in is owned by us, or
	 * not. It is described as "owned" if it is used by one of the polygons
	 * composing the board.
	 */
	private boolean isOwned(Node n) {
		return getNodes().contains(n);
	}
	
	/**
	 * A caching frontend to the <code>visibilityTest</code> function. It
	 * utilizes parallel navigation meshes, one for nodes on this board, and one
	 * for nodes not on‎ this map, but that were previously asked about. The mesh
	 * for nodes not on this map may be periodically garbage collected from,
	 * while the mesh for nodes on this map is pretty much guarenteed to remain
	 * non-garbage collected.
	 */
	public boolean isVisible(Node a, Node b) {
		
		// If they're the same (using non-strict equality), they must be visible
		if(a.equals(b, false)) {
			return true;
		}
		
		// some helper variables
		Map<Node, Boolean> aNavigationMesh = getNavigationMesh(a);
		Map<Node, Boolean> bNavigationMesh = getNavigationMesh(b);
		
		// attempt to perform caching lookups
		if(aNavigationMesh.containsKey(b)) {
			return aNavigationMesh.get(b).booleanValue();
		} if(bNavigationMesh.containsKey(a)) {
			return bNavigationMesh.get(a).booleanValue();
		}
		
		// perform test
		boolean result = visibilityTest(a, b);
		
		// Cache result, storing unowned data only in the unowned cache
		if(!isOwned(a) || isOwned(b)) {
			aNavigationMesh.put(b, result);
		} if(!isOwned(b) || isOwned(a)) {
			bNavigationMesh.put(a, result);
		}
		return result;
	}
	
	protected boolean visibilityTest(Node a, Node b) {
		Line directLine = new Line(a, b);
		for(Polygon p : this) {
			if(p.doesIntersectLine(directLine)) {
				return false;
			}
		}
		return true;
	}
	
	// Pathfinding stuff
	
	/**
	 * Returns a set of <code>Node</code>s to pass through in order to travel
	 * optimally from point <code>a</code> to <code>b</code>, not including
	 * <code>a</code> but including <code>b</code>. It uses Dijkstra's algorithm
	 * to compute this, and therefore guarentees a mathematically optimal path.
	 * <p/>
	 * 
	 * @param   a  The starting node to travel from.
	 * @param   b  The ending node to travel to.
	 * @return  A <code>List</code> of <code>Node</code>s to travel through in
	 *          order to travel the mathematically optimal path from <code>a
	 *          </code> to <code>b</code>. <code>null</code> if there is no
	 *          possible path from <code>a</code> to <code>b</code>.
	 */
	public List<Node> getShortestPath(Node a, Node b) {
		
		// Handle special/common cases
		if(isVisible(a, b)) { // direct is shortest
			List<Node> path = new LinkedList<Node>();
			path.add(b);
			return path;
		}
		
		Map<Node, ShortestPathInfo> shortestTo = new HashMap();
		shortestTo.put(b, null);
		for(Node i : getNodes()) {
			shortestTo.put(i, null);
		}
		
		Node startingFrom = a;
		double startingFromCost = 0.;
		while(true) {
			// find possible paths
			Set<Node> improvable = new HashSet<Node>();
			for(Node k : shortestTo.keySet()) {
				if(shortestTo.get(k) == null ||
				   !shortestTo.get(k).isMinimum()) {
					improvable.add(k);
				}
			}
			improvable.addAll(getVisible(startingFrom, b));
			
			if(improvable.size() == 0) { // nothing else we can do
				return null; // no possible path
			}
			
			// see if there is a shorter path for any of these via startingFrom
			double lowestCost = Double.POSITIVE_INFINITY;
			Node lowestCostNode = null;
			for(Node i : improvable) {
				double cost = startingFromCost + startingFrom.getDistance(i);
				if(cost < lowestCost) {
					lowestCost = cost;
					lowestCostNode = i;
				} if(shortestTo.get(i) == null) {
					shortestTo.put(i, new ShortestPathInfo(startingFrom, cost,
					                                       false));
				} else if(cost < shortestTo.get(i).getCost()) {
					// reuse and recycle that object if at all possible :-P
					//       __
					//      /  \
					//     / /\ \
					//    /\/ \  /     Bad ASCII-Art recycling symbol
					//   /  \  \/\
					//   / /    \ \
					//  / /__/|__\ \
					// |____(  _____|
					//       \|
					ShortestPathInfo info = shortestTo.get(i);
					info.setGoesThrough(startingFrom);
					info.setCost(cost);
				}
			}
			shortestTo.get(lowestCostNode).setIsMinimum(true);
			
			if(lowestCostNode.equals(b)) { // we're done! wrap it up.
				LinkedList<Node> path = new LinkedList<Node>();
				Node n = b;
				while(n != a) {
					path.addFirst(n);
					n = shortestTo.get(n).getGoesThrough();
				}
				return path;
			}
			startingFrom = lowestCostNode;
			startingFromCost = lowestCost;
		}
	}
	
	private static class ShortestPathInfo {
		private Node goesThrough;
		private double cost;
		private boolean isMinimum;
		
		public ShortestPathInfo(Node goesThrough, double cost,
		                        boolean isMinimum) {
			this.goesThrough = goesThrough;
			this.cost = cost;
			this.isMinimum = isMinimum;
		}
		
		public Node getGoesThrough() {
			return goesThrough;
		}
		
		public double getCost() {
			return cost;
		}
		
		public boolean isMinimum() {
			return isMinimum;
		}
		
		public boolean getIsMinimum() {
			return isMinimum();
		}
		
		public void setGoesThrough(Node goesThrough) {
			this.goesThrough = goesThrough;
		}
		
		public void setCost(double cost) {
			this.cost = cost;
		}
		
		public void setIsMinimum(boolean isMinimum) {
			this.isMinimum = isMinimum;
		}
	}
	
	// Implementation of the Collection interface, along with a few extra
	// utility functions lining up with the Collection interface
	
	/**
	 * Marks all caches as "dirty", deleting them, and requring them to be
	 * rebuilt before being used again. This is called every time the polygon
	 * data composing the board is mutated.
	 */
	protected void markDirty() {
		lines = null;
		nodes = null;
		navigationMesh = null;
		unownedNavigationMesh = null;
	}
	
	@Override
	public boolean add(Polygon p) {
		markDirty();
		return polygons.add(p);
	}
	
	public boolean addAll(Polygon ... p) {
		return addAll(Arrays.asList(p));	
	}
	
	@Override
	public boolean addAll(Collection<? extends Polygon> p) {
		markDirty();
		return polygons.addAll(p);
	}
	
	@Override
	public void clear() {
		markDirty();
		polygons.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		if(o instanceof Node) {
			return getNodes().contains(o);
		} if(o instanceof Line) {
			return getLines().contains(o);
		}
		return polygons.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return polygons.containsAll(c) || getNodes().containsAll(c) ||
		       getLines().containsAll(c);
	}
	
	@Override
	public boolean equals(Object o) {
		return polygons.equals(o);
	}
	
	@Override
	public int hashCode() {
		return polygons.hashCode();
	}
	
	@Override
	public boolean isEmpty() {
		return polygons.isEmpty();	
	}
	
	@Override
	public Iterator<Polygon> iterator() {
		return polygons.iterator();
	}
	
	@Override
	public boolean remove(Object o) {
		return polygons.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return polygons.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return polygons.retainAll(c);
	}
	
	@Override
	public int size() {
		return polygons.size();
	}
	
	public int sizeNodes() {
		if(lines != null) { return getLines().size(); }
		return getNodes().size();
	}
	
	public int sizeLines() {
		if(nodes != null) { return getNodes().size(); }
		return getLines().size();
	}
	
	@Override
	public Object[] toArray() {
		return polygons.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return polygons.toArray(a);
	}
	
	public String toString() {
		return Arrays.toString(polygons.toArray(new String[polygons.size()]));
	}
}
