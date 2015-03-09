package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.*;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MetadataWrapper extends AbstractTypeImpl implements Type, ApplyableType, OperatableType, RecordType, MetaType {
	private final Type inner;
	private final Reference<Value> metadata;
	private final Reference<DeclSequence> keywordDecls;

	public MetadataWrapper(Type inner, Reference<Value> metadata, Reference<DeclSequence> keywords) {
		this.inner = inner;
		this.metadata = metadata;
		this.keywordDecls = keywords;
	}
	
	@Override
	public String toString() {
		return TreeWriter.writeToString(this);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(inner);
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return inner.subtype(other, subtypes);
	}

	@Override
	public boolean subtype(Type other) {
		return inner.subtype(other);
	}

	@Override
	public boolean isSimple() {
		return inner.isSimple();
	}

	@Override
	public Map<String, Type> getChildren() {
		return inner.getChildren();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return new MetadataWrapper(inner.cloneWithChildren(newChildren), metadata, this.keywordDecls);
	}

	//TODO
	@Override
	public Type checkApplication(Application application, Environment env) {
		if (inner instanceof ApplyableType)
			return ((ApplyableType) inner).checkApplication(application, env);
		else
			throw new RuntimeException();
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		if (inner instanceof OperatableType)
			return ((OperatableType) inner).checkOperator(opExp, env);
		else
			throw new RuntimeException();
	}

	@Override
	public TypeBinding getInnerType(String name) {
		if (inner instanceof RecordType)
			return ((RecordType) inner).getInnerType(name);
		else
			throw new RuntimeException();
	}

	@Override
	public Value getMetaObj() {
		return this.metadata.get();
	}

	public Type getInner() { return inner; }
	
	public Reference<Value> lookupKeywordMeta(String name) {
		Iterator<Declaration> it = (this.keywordDecls.get()).getDeclIterator().iterator();
		while (it.hasNext()) {
			KeywordDeclaration thisItem = (KeywordDeclaration)it.next();
			if (thisItem.getName().equals(name)) {
				return thisItem.getMetaObj();
			}
		}
		throw new RuntimeException("Not able to find keyword: " + name);
	}
}
