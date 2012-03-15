package de.fuberlin.wiwiss.d2rq.algebra;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.fuberlin.wiwiss.d2rq.expr.Expression;
import de.fuberlin.wiwiss.d2rq.sql.ConnectedDB;

/**
 * TODO Describe this type
 * TODO Add uniqueConstraints()
 * TODO Explicitly list tables!!!
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class Relation implements RelationalOperators {

	public final static int NO_LIMIT = -1;
	
	public static Relation createSimpleRelation(
			ConnectedDB database, Attribute[] attributes) {
		return new RelationImpl(database, AliasMap.NO_ALIASES, Expression.TRUE,
				Collections.<Join>emptySet(), 
				new HashSet<ProjectionSpec>(Arrays.asList(attributes)), 
				false, null, false, -1, -1);
	}
	
	public static Relation EMPTY = new Relation() {
		public ConnectedDB database() { return null; }
		public AliasMap aliases() { return AliasMap.NO_ALIASES; }
		public Set<Join> joinConditions() { return Collections.<Join>emptySet(); }
		public Set<Join> leftJoinConditions() { return Collections.<Join>emptySet(); }
		public Expression condition() { return Expression.FALSE; }
		public Set<ProjectionSpec> projections() { return Collections.<ProjectionSpec>emptySet(); }
		public Relation select(Expression condition) { return this; }
		public Relation renameColumns(ColumnRenamer renamer) { return this; }
		public Relation project(Set<? extends ProjectionSpec>  projectionSpecs) { return this; }
		public boolean isUnique() { return true; }
		public String toString() { return "Relation.EMPTY"; }
		public Attribute order() { return null; }
		public boolean orderDesc() { return false; }
		public int limit() { return Relation.NO_LIMIT; }
		public int limitInverse() { return Relation.NO_LIMIT; }
	};
	public static Relation TRUE = new Relation() {
		public ConnectedDB database() { return null; }
		public AliasMap aliases() { return AliasMap.NO_ALIASES; }
		public Set<Join> joinConditions() { return Collections.<Join>emptySet(); }
		public Set<Join> leftJoinConditions() { return Collections.<Join>emptySet(); }
		public Expression condition() { return Expression.TRUE; }
		public Set<ProjectionSpec> projections() { return Collections.<ProjectionSpec>emptySet(); }
		public Relation select(Expression condition) {
			if (condition.isFalse()) return Relation.EMPTY;
			if (condition.isTrue()) return Relation.TRUE;
			// TODO This is broken; we need to evaluate the expression, but we don't
			// have a ConnectedDB available here so we can't really do it
			return Relation.TRUE;
		}
		public Relation renameColumns(ColumnRenamer renamer) { return this; }
		public Relation project(Set<? extends ProjectionSpec> projectionSpecs) { return this; }
		public boolean isUnique() { return true; }
		public String toString() { return "Relation.TRUE"; }
		public Attribute order() { return null; }
		public boolean orderDesc() { return false; }
		public int limit() { return Relation.NO_LIMIT; }
		public int limitInverse() { return Relation.NO_LIMIT; }
	};

	// TODO Can we remove this, and maybe pass the database around in an ARQ Context object?
	public abstract ConnectedDB database();
	
	/**
	 * The tables that are used to set up this relation, both in
	 * their aliased form, and with their original physical names.
	 * @return All table aliases required by this relation
	 */
	public abstract AliasMap aliases();
	
	/**
	 * Returns the join conditions that must hold between the tables
	 * in the relation.
	 * @return A set of {@link Join}s 
	 */
	public abstract Set<Join> joinConditions();

	/**
	 * Returns the leftjoin conditions that must hold between the tables
	 * in the relation.
	 * @return A set of {@link Join}s 
	 */
	public abstract Set<Join> leftJoinConditions();
	
	/**
	 * An expression that must be satisfied for all tuples in the
	 * relation.
	 * @return An expression; {@link Expression#TRUE} indicates no condition
	 */
	public abstract Expression condition();
	
	/**
	 * The attributes or expressions that the relation is projected to.
	 * @return A set of {@link ProjectionSpec}s
	 */
	public abstract Set<ProjectionSpec> projections();
	
	public abstract boolean isUnique();
	
	/**
	 * The database used to sort the SQL result set
	 * @return The database column name
	 */
	public abstract Attribute order();

	/**
	 * The sort order for the SQL result set
	 * @return <code>true</code> if descending, <code>false</code> if ascending order
	 */
	public abstract boolean orderDesc();

	/**
	 * The limit clause for the SQL result set
	 * @return number of records to return
	 */
	public abstract int limit();

	/**
	 * The limit clause for the SQL result set describing the inverse relation
	 * @return number of records to return
	 */
	public abstract int limitInverse();

	public Set<Attribute> allKnownAttributes() {
		Set<Attribute> results = new HashSet<Attribute>();
		results.addAll(condition().attributes());
		for (Join join: joinConditions()) {
			results.addAll(join.attributes1());
			results.addAll(join.attributes2());
		}
		for (ProjectionSpec projection: projections()) {
			results.addAll(projection.requiredAttributes());
		}
		return results;
	}

	public Set<RelationName> tables() {
		Set<RelationName> results = new HashSet<RelationName>();
		for (Attribute attribute: allKnownAttributes()) {
			results.add(attribute.relationName());
		}
		return results;
	}
	
	public boolean isTrivial() {
		return projections().isEmpty() && condition().isTrue() && joinConditions().isEmpty();
	}
		
	public static int combineLimits(int limit1, int limit2) {
	    if(limit1==Relation.NO_LIMIT) {
	        return limit2;
	    } else if(limit2==Relation.NO_LIMIT) {
	        return limit1;
	    }
	    return Math.min(limit1, limit2);
	}
}