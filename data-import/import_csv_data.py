import csv
import mysql.connector
from mysql.connector import Error

# Database configuration
db_config = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'shuju',
    'charset': 'utf8mb4'
}

csv_path = r'C:\Users\19012\Desktop\2'

def import_csv_to_db():
    try:
        # Connect to database
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()
        
        print("Connected to MySQL database successfully!")
        
        # Clear existing data
        print("\nClearing existing data...")
        cursor.execute("DELETE FROM production_order")
        cursor.execute("DELETE FROM product")
        cursor.execute("DELETE FROM supplier")
        cursor.execute("DELETE FROM product_bom")
        cursor.execute("DELETE FROM budget_execution")
        cursor.execute("DELETE FROM manufacturing_cost_detail")
        cursor.execute("DELETE FROM purchase_price_history")
        cursor.execute("DELETE FROM purchase_order")
        connection.commit()
        print("Existing data cleared.")
        
        # 1. Import products
        print("\n1. Importing products...")
        with open(f'{csv_path}\\product_code.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    "INSERT INTO product (product_code, product_name, unit) VALUES (%s, %s, %s)",
                    (row['product_code'], row['product_name'], row['unit'])
                )
        connection.commit()
        print(f"   Imported {cursor.rowcount} products")
        
        # 2. Import suppliers
        print("\n2. Importing suppliers...")
        with open(f'{csv_path}\\supplier.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    "INSERT INTO supplier (supplier_id, supplier_name, credit_grade) VALUES (%s, %s, %s)",
                    (row['supplier_id'], row['supplier_name'], row['credit_grade'])
                )
        connection.commit()
        print(f"   Imported {cursor.rowcount} suppliers")
        
        # 3. Import production orders
        print("\n3. Importing production orders...")
        count = 0
        with open(f'{csv_path}\\production_order.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    """INSERT INTO production_order 
                       (prod_order_id, product_code, plan_quantity, actual_quantity, start_date, end_date, 
                        material_cost, labor_cost, manu_cost, total_cost, status) 
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    (
                        row['prod_order_id'],
                        row['product_code'],
                        int(row['plan_quantity(吨)']),
                        int(row['actual_quantity(吨)']),
                        row['start_date'],
                        row['end_date'],
                        int(row['material_cost(元)']),
                        int(row['labor_cost(元)']),
                        int(row['manu_cost(元)']),
                        int(row['total_cost(元)']),
                        'completed'
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} production orders")
        
        # 4. Import product BOM
        print("\n4. Importing product BOM...")
        count = 0
        with open(f'{csv_path}\\product_bom.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    """INSERT INTO product_bom 
                       (bom_id, product_code, product_name, material_code, material_name, quantity_per_unit, valid_from) 
                       VALUES (%s, %s, %s, %s, %s, %s, %s)""",
                    (
                        row['bom_id'],
                        row['product_code'],
                        row['product_name'],
                        row['material_code'],
                        row['material_name'],
                        float(row['quantity_per_unit(吨/吨)']),
                        row['valid_from']
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} BOM records")
        
        # 5. Import budget execution
        print("\n5. Importing budget execution...")
        count = 0
        with open(f'{csv_path}\\budget_execution_base.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    """INSERT INTO budget_execution 
                       (fiscal_year, period, budget_item, budget_amount, actual_amount, variance, variance_rate) 
                       VALUES (%s, %s, %s, %s, %s, %s, %s)""",
                    (
                        int(row['fiscal_year']),
                        row['period'],
                        row['budget_item'],
                        float(row['budget_amount']),
                        float(row['actual_amount']),
                        float(row['variance']),
                        float(row['variance_rate'])
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} budget execution records")
        
        # 6. Import manufacturing cost detail
        print("\n6. Importing manufacturing cost detail...")
        count = 0
        with open(f'{csv_path}\\manufacturing_cost_detail.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                cursor.execute(
                    """INSERT INTO manufacturing_cost_detail 
                       (cost_date, cost_category, amount, production_line, approver) 
                       VALUES (%s, %s, %s, %s, %s)""",
                    (
                        row['cost_date'],
                        row['cost_category'],
                        float(row['amount(元)']),
                        row['production_line'],
                        row['approver']
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} manufacturing cost records")
        
        # 7. Import purchase price history
        print("\n7. Importing purchase price history...")
        count = 0
        with open(f'{csv_path}\\purchase_price_history.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                remark = row.get('remark', '') if row.get('remark', '') else None
                cursor.execute(
                    """INSERT INTO purchase_price_history 
                       (material_code, order_date, supplier_id, unit_price, remark) 
                       VALUES (%s, %s, %s, %s, %s)""",
                    (
                        row['material_code'],
                        row['order_date'],
                        row['supplier_id'],
                        float(row['unit_price(元/吨)']),
                        remark
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} purchase price records")
        
        # 8. Import purchase orders from approval_status
        print("\n8. Importing purchase orders...")
        count = 0
        with open(f'{csv_path}\\approval_status.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                status = 'approved' if row['approval_status'] == '已批' else 'pending'
                cursor.execute(
                    """INSERT INTO purchase_order 
                       (order_id, order_date, supplier_id, material_code, material_name, quantity, unit_price, total_amount, approval_status) 
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    (
                        row['order_id'],
                        row['order_date'],
                        row['supplier_id'],
                        row['material_code'],
                        row['material_name'],
                        float(row['quantity(吨)']),
                        float(row['unit_price(元/吨)']),
                        float(row['total_amount(元)']),
                        status
                    )
                )
                count += 1
        connection.commit()
        print(f"   Imported {count} purchase orders")
        
        print("\n" + "="*60)
        print("All CSV data imported successfully!")
        print("="*60)
        
        # Verify data
        print("\nData verification:")
        cursor.execute("SELECT COUNT(*) FROM product")
        print(f"  Products: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM supplier")
        print(f"  Suppliers: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM production_order")
        print(f"  Production Orders: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM product_bom")
        print(f"  Product BOM: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM budget_execution")
        print(f"  Budget Execution: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM manufacturing_cost_detail")
        print(f"  Manufacturing Cost: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM purchase_price_history")
        print(f"  Purchase Price History: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM purchase_order")
        print(f"  Purchase Orders: {cursor.fetchone()[0]}")
        
    except Error as e:
        print(f"Error: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("\nMySQL connection closed.")

if __name__ == "__main__":
    import_csv_to_db()
