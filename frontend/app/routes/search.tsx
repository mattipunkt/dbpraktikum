import { useEffect, useState } from "react";
import type { Route } from "./+types/home";
import type { Product } from "~/components/productlist/productlist";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Suche deine Produkte!" },
    {
      name: "Search",
      content: "product lists shows all your favorite products!",
    },
  ];
}

export default function Search() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Product[]>([]);

  useEffect(() => {
    if (!query) {
      setResults([]); // leere Suche => keine Ergebnisse
      return;
    }

    const timeoutId = setTimeout(() => {
      fetch(`http://localhost:8080/produkte/${encodeURIComponent(query)}`)
        .then((res) => res.json())
        .then((data) => setResults(data))
        .catch(() => setResults([]));
    }, 500); // 500 ms warten

    return () => clearTimeout(timeoutId); // alten Timer löschen, wenn query sich ändert
  }, [query]);

  return (
    <div>
      <div className="grid grid-cols-4 flex-items-center">
        <div className="col-span-1 flex items-center">
          <p className="font-bold text-2xl">Suche!</p>
        </div>
        <div className="col-span-3">
          <input
            className="w-full border rounded-lg p-2"
            type="text"
            placeholder={"Suche nach Produkten..."}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>
      </div>
      <div className="mt-5">
        {results.map((product) => (
          <a
            key={product.id}
            href={`/product/${product.id}`}
            className="my-10 py-2"
          >
            <div className="flex items-center justify-between first:rounded-t-2xl px-4 py-4 last:rounded-b-2xl shadow col-span-1 border transition-shadow duration-300">
              <div className="flex items-center gap-4">
                <img
                  src={product.bild}
                  alt={product.titel}
                  className="w-16 h-16 object-cover rounded-lg"
                />
                <div>
                  <p className="font-medium">{product.titel}</p>
                  <p className="text-sm text-gray-500">{product.typ}</p>
                </div>
              </div>
              <div className="text-yellow-500 font-bold">
                {product.rating} ★
              </div>
            </div>
          </a>
        ))}
      </div>
    </div>
  );
}
