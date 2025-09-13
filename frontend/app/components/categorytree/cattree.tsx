import { useCallback, useEffect, useState } from "react";

type Category = {
  id: number;
  name: string;
  kategorien?: Category[];
};

export function Category({ category }: { category: Category }) {
  return (
    <div>
      <details>
        <summary className="cursor-pointer font-medium">
          <a href={`/products?${category.id}`}>{category.name}</a>
        </summary>
        <div className="ml-4">
          {category.kategorien &&
            category.kategorien.map((child) => (
              <Category key={child.id} category={child} />
            ))}
        </div>
      </details>
    </div>
  );
}

export default function CategoryTree() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);

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
    <div>
      <div className="text-lg font-medium">Kategorie-Baum</div>
      {loading ? (
        <div>Lade Kategorien...</div>
      ) : (
        <div>
          {categories.map((cat) => (
            <Category key={cat.id} category={cat} />
          ))}
        </div>
      )}
    </div>
  );
}
