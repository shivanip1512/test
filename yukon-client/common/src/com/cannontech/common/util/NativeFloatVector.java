package com.cannontech.common.util;

/**
 * Insert the type's description here.
 * Creation date: (10/28/2000 9:18:49 PM)
 * @author: 
 */
import java.util.NoSuchElementException;

public class NativeFloatVector implements java.io.Serializable, Cloneable
{
	private int elementCount = 0;
	private int capacityIncrement = 0;



	/**
	 * The number of times this list has been <i>structurally modified</i>.
	 * Structural modifications are those that change the size of the
	 * list, or otherwise perturb it in such a fashion that iterations in
	 * progress may yield incorrect results.<p>
	 *
	 * This field is used by the iterator and list iterator implementation
	 * returned by the <tt>iterator</tt> and <tt>listIterator</tt> methods.
	 * If the value of this field changes unexpectedly, the iterator (or list
	 * iterator) will throw a <tt>ConcurrentModificationException</tt> in
	 * response to the <tt>next</tt>, <tt>remove</tt>, <tt>previous</tt>,
	 * <tt>set</tt> or <tt>add</tt> operations.  This provides
	 * <i>fail-fast</i> behavior, rather than non-deterministic behavior in
	 * the face of concurrent modification during iteration.<p>
	 *
	 * <b>Use of this field by subclasses is optional.</b> If a subclass
	 * wishes to provide fail-fast iterators (and list iterators), then it
	 * merely has to increment this field in its <tt>add(int, Object)</tt> and
	 * <tt>remove(int)</tt> methods (and any other methods that it overrides
	 * that result in structural modifications to the list).  A single call to
	 * <tt>add(int, Object)</tt> or <tt>remove(int)</tt> must add no more than
	 * one to this field, or the iterators (and list iterators) will throw
	 * bogus <tt>ConcurrentModificationExceptions</tt>.  If an implementation
	 * does not wish to provide fail-fast iterators, this field may be
	 * ignored.
	 */
	protected transient int modCount = 0;

	// The possible native types
	private float[] floatElementData = null;

/**
 * NativeVector constructor comment.
 */
public NativeFloatVector() 
{
	this(10);
}
/**
 * Constructs an empty vector with the specified initial capacity and
 * capacity increment. 
 */
public NativeFloatVector(int initialCapacity) 
{
	this(initialCapacity, 0);
}
/**
 * Constructs an empty vector with the specified initial capacity and
 * capacity increment. 
 */
public NativeFloatVector(int initialCapacity, int capacityIncrement) 
{
	super();
	
	if (initialCapacity < 0)
		throw new IllegalArgumentException("Illegal Capacity: "+
											   initialCapacity);
		
	this.floatElementData = new float[initialCapacity];
	this.capacityIncrement = capacityIncrement;
}
/**
 * add method comment.
 */
public boolean add(float i) 
{
	ensureCapacityHelper(elementCount + 1);
	
	floatElementData[elementCount++] = i;
	
	return true;
}
/**
 * add method comment.
 */
public void add(int index, float element) 
{
	insertElementAt(element, index);
}
/**
 * Adds the specified component to the end of this vector, 
 * increasing its size by one. The capacity of this vector is 
 * increased if its size becomes greater than its capacity. <p>
 *
 * This method is identical in functionality to the add(Object) method
 * (which is part of the List interface).
 */
public synchronized void addElement(float element) 
{
	//modCount++;
	ensureCapacityHelper(elementCount + 1);
	floatElementData[elementCount++] = element;
}
/**
 * clear method comment.
 */
public void clear() 
{
	removeAllElements();
}
/**
 * Returns a clone of this vector. The copy will contain a
 * reference to a clone of the internal data array, not a reference 
 * to the original internal data array of this <tt>NativeIntVector</tt> object. 
 */
public synchronized Object clone() 
{
	try 
	{ 
	    NativeFloatVector v = (NativeFloatVector)super.clone();
	    v.floatElementData = new float[elementCount];
	    System.arraycopy(floatElementData, 0, v.floatElementData, 0, elementCount);
	    //v.modCount = 0;
	    return v;
	} 
	catch (CloneNotSupportedException e) 
	{ 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
}
/**
 * contains method comment.
 */
public boolean contains(float i) 
{
	return indexOf(i, 0) >= 0;
}
/**
 * Copies the components of this vector into the specified array. The 
 * item at index <tt>k</tt> in this vector is copied into component 
 * <tt>k</tt> of <tt>anArray</tt>. The array must be big enough to hold 
 * all the objects in this vector, else an 
 * <tt>IndexOutOfBoundsException</tt> is thrown.
 */
public synchronized void copyInto(float anArray[]) 
{
	System.arraycopy(floatElementData, 0, anArray, 0, elementCount);
}
/**
 * Returns the component at the specified index.<p>
 */
public synchronized float elementAt(int index) 
{
	if (index >= elementCount) 
	{
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
	}
	
	/* Since try/catch is free, except when the exception is thrown,
	   put in this extra try/catch to catch negative indexes and
	   display a more informative error message.  This might not
	   be appropriate, especially if we have a decent debugging
	   environment - JP. */
	try 
	{
	    return floatElementData[index];
	} 
	catch (ArrayIndexOutOfBoundsException e) 
	{
	    throw new ArrayIndexOutOfBoundsException(index + " < 0");
	}
}
/**
 * This implements the unsynchronized semantics of ensureCapacity.
 * Synchronized methods in this class can internally call this 
 * method for ensuring capacity without incurring the cost of an 
 * extra synchronization.
 */ 
private void ensureCapacityHelper(int minCapacity) 
{
	int oldCapacity = floatElementData.length;
	
	if (minCapacity > oldCapacity) 
	{
	    float oldData[] = floatElementData;
	    
	    int newCapacity = (capacityIncrement > 0) ?
	    		(oldCapacity + capacityIncrement) : (oldCapacity * 2);
	    		
	    if (newCapacity < minCapacity) 
	    {
			newCapacity = minCapacity;
		}
	    
	    floatElementData = new float[newCapacity];
	    System.arraycopy(oldData, 0, floatElementData, 0, elementCount);
	}
}
/**
 * Compares the specified Object with this Vector for equality.  Returns
 * true if and only if the specified Object is also a List, both Lists
 * have the same size, and all corresponding pairs of elements in the two
 * Lists are <em>equal</em>.  (Two elements <code>e1</code> and
 * <code>e2</code> are <em>equal</em> if <code>(e1==null ? e2==null :
 * e1.equals(e2))</code>.)  In other words, two Lists are defined to be
 * equal if they contain the same elements in the same order.
 */
public synchronized boolean equals(Object o) 
{
	return super.equals(o);
}
/**
 * Returns the first component (the item at index <tt>0</tt>) of 
 * this vector.
 *
 */
public synchronized float firstElement() 
{
	if (elementCount == 0) 
	{
	    throw new NoSuchElementException();
	}
	
	return floatElementData[0];
}
/**
 * indexOf method comment.
 */
public int indexOf(float i) 
{
	return indexOf(i, 0);
}
/**
 * Searches for the first occurence of the given argument, beginning 
 * the search at <code>index</code>, and testing for equality using 
 * the <code>equals</code> method. 
 */
public synchronized int indexOf(float value, int index) 
{
	for (int i = index; i < elementCount; i++)
		if (floatElementData[i] == value)
	    	return i;
	
	return -1;
}
/**
 * Inserts the specified object as a component in this vector at the 
 * specified index.
 */
public synchronized void insertElementAt(float element, int index) 
{
	//modCount++;
	if (index >= elementCount + 1) 
	{
	    throw new ArrayIndexOutOfBoundsException(index
						     + " > " + elementCount);
	}
	
	ensureCapacityHelper(elementCount + 1);
	
	System.arraycopy(floatElementData, index, floatElementData, index + 1, elementCount - index);
	floatElementData[index] = element;
	elementCount++;
}
/**
 * isEmpty method comment.
 */
public boolean isEmpty() 
{
	return elementCount == 0;
}
/**
 * Returns the last component of the vector.
 */
public synchronized float lastElement() 
{
	if (elementCount == 0) 
	{
	    throw new NoSuchElementException();
	}
	
	return floatElementData[elementCount - 1];
}
/**
 * Searches backwards for the specified object, starting from the 
 * specified index, and returns an index to it. 
 */
public synchronized int lastIndexOf(float element, int index) 
{
	for (int i = index; i >= 0; i--)
		if (floatElementData[i] == element)
	    	return i;
	    	
	return -1;
}
/**
 * lastIndexOf method comment.
 */
public int lastIndexOf(int i) 
{
	return indexOf(i, 0);
}
/**
 * Removes the element at the specified position in this NativeVector.
 */
public synchronized float remove(int index) 
{
	//modCount++;
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	    
	float oldValue = floatElementData[index];

	int numMoved = elementCount - index - 1;
	
	if (numMoved > 0)
	    System.arraycopy(floatElementData, index+1, floatElementData, index, 
		    numMoved);

	--elementCount;
	

	return oldValue;
}
/**
 * Removes all components from this vector and sets its size to zero.<p>
**/
public synchronized void removeAllElements() 
{
	// Let gc do its work
	floatElementData = null;

	elementCount = 0;
}
/**
 * Removes the first (lowest-indexed) occurrence of the argument 
 * from this vector. If the object is found in this vector, each 
 * component in the vector with an index greater or equal to the 
 * object's index is shifted downward to have an index one smaller 
 * than the value it had previously.<p>
 */
public synchronized boolean removeElement(float element) 
{
	//modCount++;
	int i = indexOf(element);
	
	if (i >= 0) 
	{
	    removeElementAt(i);
	    return true;
	}
	
	return false;
}
/**
 * Deletes the component at the specified index. Each component in 
 * this vector with an index greater or equal to the specified 
 * <code>index</code> is shifted downward to have an index one 
 * smaller than the value it had previously. The size of this vector 
 * is decreased by <tt>1</tt>.<p>
 *
 * The index must be a value greater than or equal to <code>0</code> 
 * and less than the current size of the vector. <p>
 *
 * This method is identical in functionality to the remove method
 * (which is part of the List interface).  Note that the remove method
 * returns the old value that was stored at the specified position.
 */
public synchronized void removeElementAt(int index) 
{
	//modCount++;
	if (index >= elementCount) 
	{
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + 
						     elementCount);
	}
	else if (index < 0) 
	{
	    throw new ArrayIndexOutOfBoundsException(index);
	}
	
	int j = elementCount - index - 1;
	if (j > 0) 
	{
	    System.arraycopy(floatElementData, index + 1, floatElementData, index, j);
	}
	
	elementCount--;
}
/**
 * Removes from this List all of the elements whose index is between
 * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
 * elements to the left (reduces their index).
 * This call shortens the ArrayList by (toIndex - fromIndex) elements.  (If
 * toIndex==fromIndex, this operation has no effect.)
 */
protected void removeRange(int fromIndex, int toIndex) 
{
	//modCount++;
	int newElementCount = elementCount - (toIndex-fromIndex);
	float[] result = new float[newElementCount];
	 
	int numMoved = elementCount - toIndex;
	System.arraycopy(floatElementData, toIndex, result, 
						0, numMoved);

	elementCount = newElementCount;
	floatElementData = result;
	
}
/**
 * Replaces the element at the specified position in this NativeVector
 * with the specified element.
 */
public synchronized float set(int index, float element) 
{
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);

	float oldValue = floatElementData[index];
	floatElementData[index] = element;
	return oldValue;
}
/**
 * Sets the component at the specified <code>index</code> of this 
 * vector to be the specified object. The previous component at that 
 * position is discarded.<p>
 *
 * The index must be a value greater than or equal to <code>0</code> 
 * and less than the current size of the vector. <p>
 *
 * This method is identical in functionality to the set method
 * (which is part of the List interface). Note that the set method reverses
 * the order of the parameters, to more closely match array usage.  Note
 * also that the set method returns the old value that was stored at the
 * specified position.
 */
public synchronized void setElementAt(float element, int index) 
{
	if (index >= elementCount) 
	{
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + 
						     elementCount);
	}
	
	floatElementData[index] = element;
}
/**
 * Sets the size of this vector. If the new size is greater than the 
 * current size, new <code>null</code> items are added to the end of 
 * the vector. If the new size is less than the current size, all 
 * components at index <code>newSize</code> and greater are discarded.
 */
public synchronized void setSize(int newSize) 
{
	//modCount++;
	if (newSize > elementCount) 
	{
	    ensureCapacityHelper(newSize);
	} 
	else 
	{
		float[] result = new float[newSize];
	    for (int i = 0; i < newSize; i++) 
	    {
			result[i] = floatElementData[i];
	    }

	    floatElementData = result;
	}
	
	elementCount = newSize;
}
/**
 * size method comment.
 */
public int size() 
{
	return elementCount;
}
/**
 * toArray method comment.
 */
public float[] toArray() 
{
	float[] result = new float[elementCount];	
	System.arraycopy(floatElementData, 0, result, 0, elementCount);
	
	return result;
}
/**
 * Returns a string representation of this NativeIntVector, containing
 * the String representation of each element.
 */
public synchronized String toString() 
{
	return super.toString();
}
/**
 * Trims the capacity of this vector to be the vector's current 
 * size. If the capacity of this cector is larger than its current 
 * size, then the capacity is changed to equal the size by replacing 
 * its internal data array, kept in the field <tt>elementData</tt>, 
 * with a smaller one. An application can use this operation to 
 * minimize the storage of a vector. 
 */
public synchronized void trimToSize() 
{
	//modCount++;
	int oldCapacity = floatElementData.length;
	if (elementCount < oldCapacity) 
	{
	    float oldData[] = floatElementData;
	    floatElementData = new float[elementCount];
	    System.arraycopy(oldData, 0, floatElementData, 0, elementCount);
	}
}
}
