package com.cannontech.common.util;

/**
 * Insert the type's description here.
 * Creation date: (10/28/2000 9:18:49 PM)
 * @author: 
 */
import java.util.NoSuchElementException;

public class NativeIntVector implements java.io.Serializable, Cloneable
{
	private int elementCount = 0;
	private int capacityIncrement = 0;

	// The possible native types
	private int[] intElementData = null;

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

/**
 * NativeVector constructor comment.
 */
public NativeIntVector() 
{
	this(10);
}
/**
 * Constructs an empty vector with the specified initial capacity and
 * capacity increment. 
 */
public NativeIntVector(int initialCapacity) 
{
	this(initialCapacity, 0);
}
/**
 * Constructs an empty vector with the specified initial capacity and
 * capacity increment. 
 */
public NativeIntVector(int initialCapacity, int capacityIncrement) 
{
	super();
	
	if (initialCapacity < 0)
		throw new IllegalArgumentException("Illegal Capacity: "+
											   initialCapacity);
		
	this.intElementData = new int[initialCapacity];
	this.capacityIncrement = capacityIncrement;
}
/**
 * add method comment.
 */
public boolean add(int i) 
{
	ensureCapacityHelper(elementCount + 1);
	
	intElementData[elementCount++] = i;
	
	return true;
}
/**
 * add method comment.
 */
public void add(int index, int element) 
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
public synchronized void addElement(int element) 
{
	//modCount++;
	ensureCapacityHelper(elementCount + 1);
	intElementData[elementCount++] = element;
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
	    NativeIntVector v = (NativeIntVector)super.clone();
	    v.intElementData = new int[elementCount];
	    System.arraycopy(intElementData, 0, v.intElementData, 0, elementCount);
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
public boolean contains(int i) 
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
public synchronized void copyInto(int anArray[]) 
{
	System.arraycopy(intElementData, 0, anArray, 0, elementCount);
}
/**
 * Returns the component at the specified index.<p>
 */
public synchronized int elementAt(int index) 
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
	    return intElementData[index];
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
	int oldCapacity = intElementData.length;
	
	if (minCapacity > oldCapacity) 
	{
	    int oldData[] = intElementData;
	    
	    int newCapacity = (capacityIncrement > 0) ?
	    		(oldCapacity + capacityIncrement) : (oldCapacity * 2);
	    		
	    if (newCapacity < minCapacity) 
	    {
			newCapacity = minCapacity;
		}
	    
	    intElementData = new int[newCapacity];
	    System.arraycopy(oldData, 0, intElementData, 0, elementCount);
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
public synchronized int firstElement() 
{
	if (elementCount == 0) 
	{
	    throw new NoSuchElementException();
	}
	
	return intElementData[0];
}
/**
 * indexOf method comment.
 */
public int indexOf(int i) 
{
	return indexOf(i, 0);
}
/**
 * Searches for the first occurence of the given argument, beginning 
 * the search at <code>index</code>, and testing for equality using 
 * the <code>equals</code> method. 
 */
public synchronized int indexOf(int value, int index) 
{
	for (int i = index; i < elementCount; i++)
		if (intElementData[i] == value)
	    	return i;
	
	return -1;
}
/**
 * Inserts the specified object as a component in this vector at the 
 * specified index.
 */
public synchronized void insertElementAt(int value, int index) 
{
	//modCount++;
	if (index >= elementCount + 1) 
	{
	    throw new ArrayIndexOutOfBoundsException(index
						     + " > " + elementCount);
	}
	
	ensureCapacityHelper(elementCount + 1);
	
	System.arraycopy(intElementData, index, intElementData, index + 1, elementCount - index);
	intElementData[index] = value;
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
public synchronized int lastElement() 
{
	if (elementCount == 0) 
	{
	    throw new NoSuchElementException();
	}
	
	return intElementData[elementCount - 1];
}
/**
 * lastIndexOf method comment.
 */
public int lastIndexOf(int i) 
{
	return indexOf(i, 0);
}
/**
 * Searches backwards for the specified object, starting from the 
 * specified index, and returns an index to it. 
 */
public synchronized int lastIndexOf(int value, int index) 
{
	for (int i = index; i >= 0; i--)
		if (intElementData[i] == value)
	    	return i;
	    	
	return -1;
}
/**
 * Removes the element at the specified position in this NativeVector.
 */
public synchronized int remove(int index) 
{
	//modCount++;
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	    
	int oldValue = intElementData[index];

	int numMoved = elementCount - index - 1;
	
	if (numMoved > 0)
	    System.arraycopy(intElementData, index+1, intElementData, index, 
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
	intElementData = null;

	elementCount = 0;
}
/**
 * Removes the first (lowest-indexed) occurrence of the argument 
 * from this vector. If the object is found in this vector, each 
 * component in the vector with an index greater or equal to the 
 * object's index is shifted downward to have an index one smaller 
 * than the value it had previously.<p>
 */
public synchronized boolean removeElement(int element) 
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
	    System.arraycopy(intElementData, index + 1, intElementData, index, j);
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
	int[] result = new int[newElementCount];
	 
	int numMoved = elementCount - toIndex;
	System.arraycopy(intElementData, toIndex, result, 
						0, numMoved);

	elementCount = newElementCount;
	intElementData = result;
	
}
/**
 * Replaces the element at the specified position in this NativeVector
 * with the specified element.
 */
public synchronized int set(int index, int element) 
{
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);

	int oldValue = intElementData[index];
	intElementData[index] = element;
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
public synchronized void setElementAt(int element, int index) 
{
	if (index >= elementCount) 
	{
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + 
						     elementCount);
	}
	
	intElementData[index] = element;
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
		int[] result = new int[newSize];
	    for (int i = 0; i < newSize; i++) 
	    {
			result[i] = intElementData[i];
	    }

	    intElementData = result;
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
public int[] toArray() 
{
	int[] result = new int[elementCount];	
	System.arraycopy(intElementData, 0, result, 0, elementCount);
	
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
	int oldCapacity = intElementData.length;
	if (elementCount < oldCapacity) 
	{
	    int oldData[] = intElementData;
	    intElementData = new int[elementCount];
	    System.arraycopy(oldData, 0, intElementData, 0, elementCount);
	}
}
}
