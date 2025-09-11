import { useEffect, useState, useCallback } from "react";
import { ProductCard } from "./productcard";

type Product = {
  id: number;
  titel: string;
  typ: string;
  bild?: string;
  rating: number;
};

export default function ProductList() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const fetchProducts = useCallback(() => {
    if (loading || !hasMore) return;
    setLoading(true);
    fetch(`http://127.0.0.1:8080/produkte?page=${page}`)
      .then((res) => res.json())
      .then((data) => {
        setProducts((prev) => [...prev, ...data.content]);
        setHasMore(!data.last); // API liefert z.B. last: true, wenn keine weiteren Seiten
        setPage((prev) => prev + 1);
      })
      .finally(() => setLoading(false));
  }, [page, loading, hasMore]);

  useEffect(() => {
    fetchProducts();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      if (
        window.innerHeight + window.scrollY >=
          document.body.offsetHeight - 200 &&
        !loading &&
        hasMore
      ) {
        fetchProducts();
      }
    };
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [fetchProducts, loading, hasMore]);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 w-full mx-0">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          name={product.titel}
          rating={product.rating ?? 0}
          product_type={product.typ}
          img={product.bild}
          id={product.id}
        />
      ))}
      {loading && (
        <p className="col-span-4 text-center">Lade weitere Produkte...</p>
      )}
      {!hasMore && (
        <p className="col-span-4 text-center">Keine weiteren Produkte.</p>
      )}
    </div>
  );
}
