/*
 * Created on 14.04.2005 by Joerg Garbers, FU-Berlin
 *
 */
package de.fuberlin.wiwiss.d2rq.rdql;

import java.util.Iterator;
import java.util.List;

import de.fuberlin.wiwiss.d2rq.rdql.ExpressionTranslator.Result;
import de.fuberlin.wiwiss.d2rq.rdql.ExpressionTranslator;

/**
 * OperatorMap describes an Operator mapping from <it>rdqlOperator</it> to <it>sqlOperator</it>.
 * ArgTypes, leftTypes and rightTypes describe the allowed value types for the sqlOperator.
 * Currently we do support overloading, but do not support automatic conversion such as (5 + "7").
 * If sameType (default) then left and right operand must have same actual type.
 * If returnType=LeftType (default) then the result will have the same type as the left argument.
 * If returnType=RightType then the actual rightType.
 */
class OperatorMap {
    public String rdqlOperator; // unqualified class name
    public String sqlOperator;
    // public int argTypes; 
    public int leftTypes;
    public int rightTypes;
    public int returnType=LeftType; 
    public boolean sameType;
    public boolean unary=false;
    
    public static final int NoType=ExpressionTranslator.NoType;
    public static final int LeftRightType=16; // different type for left and right operand
    public static final int LeftType=ExpressionTranslator.LeftType;
    public static final int RightType=ExpressionTranslator.RightType;

    
    public int resultType(Result left) {
        // unary
        // ugly implementation, but technically the same
        return resultType(left,left,true); // technically
    }

    public int resultType(Result left, Result right) {
        return resultType(left,right,false);
    }
    public int resultType(Result left, Result right, boolean unaryApplication) {
        int ret=returnType;
        if (this.unary!=unaryApplication)
            return NoType;
        int leftType=left.getType();
        if ((leftTypes & leftType) == NoType )
            return NoType;
        if (returnType==LeftType)
            ret=leftType;
        if (!unary) {
            int rightType=right.getType();
            if ((rightTypes & rightType) == NoType )
                return NoType;
            if (sameType && (leftType != rightType))
                return NoType;
            if (returnType==RightType)
                ret=rightType;
        }
        return ret;
    }
    /**
     * creates an sql expression that contains sqlOperator at infix positions
     * @param op
     * @param args
     * @return
     */
    public Result applyInfix(List args) {
        StringBuffer sb=new StringBuffer("(");
        Result returnResult=ExpressionTranslator.newResult(sb,NoType);
        Result leftResult=null;
        Result rightResult=null;
        int returnType=NoType;
        Iterator it=args.iterator();
        boolean first=true;
        while (it.hasNext()) {
            Result result=(Result)it.next();
            if (first) {
                first=false;
                leftResult=result;
            } else {
                rightResult=result;
                returnType=resultType(leftResult,rightResult);
                if (returnType==NoType)
                    return null;
                returnResult.setType(returnType);
                leftResult=returnResult;
                sb.append(' ');
                sb.append(sqlOperator);
                sb.append(' ');                   
            }
            result.appendTo(sb);
        }
        sb.append(")");
        return returnResult;
    }
    
    /**
     * creates a unary sql expression
     * @param op
     * @param arg
     * @return
     */
    public Result applyUnary(Result arg) {
        StringBuffer result=new StringBuffer("(");
        int type=resultType(arg);
        if (type==NoType)
            return null;
        result.append(sqlOperator);
        result.append(" ");
        arg.appendTo(result);
        result.append(")");
        return ExpressionTranslator.newResult(result,type);
    }

}

