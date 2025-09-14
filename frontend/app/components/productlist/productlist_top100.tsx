import { useEffect, useState, useCallback } from "react";
import { ProductCard } from "./productcard";

export type Product = {
  id: number;
  titel: string;
  typ: string;
  bild?: string;
  rating: number;
};

export default function ProductList({
  categoryPath,
}: {
  categoryPath: string | null;
}) {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [k, setK] = useState(10);

  const fetchProducts = useCallback(() => {
    if (loading || !hasMore) return;
    setLoading(true);
    {
      fetch(`http://127.0.0.1:8080/produkte/top/${k}`)
        .then((res) => res.json())
        .then((data) => {
          setProducts(data);
        })
        .finally(() => setLoading(false));
    }
  }, [page, loading, hasMore, k]);

  useEffect(() => {
    fetchProducts();
    // eslint-disable-next-line
  }, [k]);

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Top-Produkte</h1>
        <div className="flex gap-2 items-center-safe">
          <p>Anzahl der Top-Produkte</p>
          <input
            type="number"
            onChange={(e) => setK(parseInt(e.target.value))}
            value={k}
            min={1}
            max={200}
            className="border border-gray-300 rounded-md px-2 py-1 text-sm"
          ></input>
        </div>
      </div>
      <div className="grid grid-cols-1">
        {products.map((product) => (
          <a href={`/product/${product.id}`} className="my-1">
            <div className="flex items-center justify-between first:rounded-t-2xl px-4 py-4 last:rounded-b-2xl shadow col-span-1 border transition-shadow duration-300">
              <h2>{product.titel}</h2>
              <div>
                <div className="text-yellow-500">
                  {"★".repeat(Math.floor(product.rating))}
                  {"☆".repeat(Math.ceil(5 - product.rating))}({product.rating})
                </div>
              </div>
            </div>
          </a>
        ))}
        {loading && (
          <p className="col-span-4 text-center">Lade weitere Produkte...</p>
        )}
        {!hasMore && (
          <p className="col-span-4 text-center">Keine weiteren Produkte.</p>
        )}
      </div>
    </div>
  );
}
