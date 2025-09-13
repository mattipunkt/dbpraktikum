import type { Route } from "./+types/home";
import ProductList from "~/components/productlist/productlist";
import { useState, useEffect } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Produktliste" },
    {
      name: "description",
      content: "product lists shows all your favorite products!",
    },
  ];
}

type Category = {
  id: number;
  name: string;
  kategorien?: Category[];
};

type CategoryProps = {
  category: Category;
  parentPath?: string;
  onSelect: (path: string) => void;
};

export function Category({
  category,
  parentPath = "",
  onSelect,
}: CategoryProps) {
  const currentPath =
    parentPath === "" ? category.name : `${parentPath}/${category.name}`;

  return (
    <div>
      <details>
        <summary
          className="cursor-pointer font-medium"
          onClick={(e) => {
            e.stopPropagation(); // verhindert bubbling
            onSelect(currentPath);
          }}
        >
          {category.name}
        </summary>
        <div className="ml-4">
          {category.kategorien &&
            category.kategorien.map((child) => (
              <Category
                key={child.id}
                category={child}
                parentPath={currentPath}
                onSelect={onSelect}
              />
            ))}
        </div>
      </details>
    </div>
  );
}

export default function Produkte() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedPath, setSelectedPath] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  const fetchCategories = () => {
    if (loading) return;
    setLoading(true);
    fetch(`http://localhost:8080/categories`)
      .then((res) => res.json())
      .then((data) => {
        setCategories(data);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  return (
    <div className="grid grid-cols-4 mx-0">
      <div className="col-span-1 mr-10 rounded-2xl bg-gray-50 px-4 py-4">
        <div>
          <div className="text-lg font-medium">Kategorie-Baum</div>
          {loading ? (
            <div>Lade Kategorien...</div>
          ) : (
            <div>
              {categories.map((cat) => (
                <Category
                  key={cat.id}
                  category={cat}
                  onSelect={(path) => {
                    setSelectedPath(path);
                    setReloadToken((prev) => prev + 1); // trigger reload
                  }}
                />
              ))}
            </div>
          )}
        </div>
      </div>
      <div className="col-span-3">
        <h1 className="text-3xl font-bold mb-4">
          Produkte {selectedPath ? `(Kategorie: ${selectedPath})` : ""}
        </h1>
        <ProductList key={reloadToken} categoryPath={selectedPath} />
      </div>
    </div>
  );
}
