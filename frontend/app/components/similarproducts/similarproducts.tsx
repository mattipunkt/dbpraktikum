import SmallProductCard from "../productlist/smallcard";
import type { Product } from "../productlist/productlist";

export default function SimilarProducts({ products }: { products: Product[] }) {
  return (
    <div>
      <h2 className="text-xl font-bold">Ähnliche Produkte</h2>
      {products && products.length > 0 ? (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 mt-4">
          {products.map((product) => (
            <SmallProductCard key={product.id} product={product} />
          ))}
        </div>
      ) : (
        <p className="text-gray-500">Keine ähnlichen Produkte gefunden.</p>
      )}
    </div>
  );
}
